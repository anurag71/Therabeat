package com.anurag.therabeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private List<AudioModel> audioDataSet;
    private LayoutInflater mInflater;
    private List<AudioModel> itemsCopy;
    int ViewPagerPosition;
    Context mContext;
    private ItemClickListener mListener;

    public AudioListAdapter(Context context, List<AudioModel> audioModelList, ItemClickListener listener, int ViewPagerPosition) {

        mInflater = LayoutInflater.from(context);
        mContext = context;
        audioDataSet = audioModelList;
        itemsCopy = new ArrayList<>();
        itemsCopy.addAll(audioDataSet);
        this.mListener = listener;
        this.ViewPagerPosition = ViewPagerPosition;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View v = mInflater.inflate(R.layout.audio_list_row, viewGroup, false);

        AudioViewHolder vh = new AudioViewHolder(mContext, audioDataSet, v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder audioViewHolder, int i) {
        if (audioDataSet.size() > 0) {
            switch (ViewPagerPosition) {
                case 2:
                    audioViewHolder.mTextView.setText(audioDataSet.get(i).getaArtist());
                    break;
                case 3:
                    audioViewHolder.mTextView.setText(audioDataSet.get(i).getaAlbum());
                    break;
                default:
                    audioViewHolder.mTextView.setText(audioDataSet.get(i).getaName());
                    break;
            }
            if (MusicListActivity.FavList.contains(audioDataSet.get(i))) {
                audioViewHolder.mCheckBox.setChecked(true);
            } else {
                audioViewHolder.mCheckBox.setChecked(false);
            }
            audioViewHolder.mCheckBox.setOnClickListener(view -> {
                if (audioViewHolder.mCheckBox.isChecked()) {
                    MusicListActivity.FavList.add(audioDataSet.get(i));
                    Toast.makeText(mContext, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    MusicListActivity.FavList.remove(audioDataSet.get(i));
                    Toast.makeText(mContext, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }

                Gson gson = new Gson();
                String json = gson.toJson(MusicListActivity.FavList);
                mContext.getSharedPreferences("Therabeat", 0).edit().putString("FavList", json).apply();
                MusicListActivity.FavRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        MusicListActivity.FavAdaptor.notifyDataSetChanged();
                    }
                });
            });


        }
    }

    public interface ItemClickListener {
        void onItemClicked(AudioModel audio);
    }

    @Override
    public int getItemCount() {
        return audioDataSet.size();
    }

    public void filter(String text) {
        audioDataSet.clear();
        if (text.isEmpty()) {
            audioDataSet.addAll(itemsCopy);
        } else {
            text = text.toLowerCase();
            for (AudioModel item : itemsCopy) {
                if (item.getaName().toLowerCase().contains(text) || item.getaAlbum().toLowerCase().contains(text) || item.getaArtist().toLowerCase().contains(text)) {
                    audioDataSet.add(item);
                }
            }
        }
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context nContext;
        List<AudioModel> audioList;
        public TextView mTextView;
        public CheckBox mCheckBox;
        private ItemClickListener mListener;

        public AudioViewHolder(Context context, List<AudioModel> audioModelList, View v, ItemClickListener mListener) {
            super(v);
            nContext = context;
            audioList = audioModelList;
            mTextView = v.findViewById(R.id.audioName);
            mCheckBox = v.findViewById(R.id.favorite);
            this.mListener = mListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int itemPosition = getAdapterPosition();
            mListener.onItemClicked(audioList.get(itemPosition));

        }
    }
}