package com.anurag.therabeat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private List<AudioModel> audioDataSet;
    private LayoutInflater mInflater;
    private List<AudioModel> itemsCopy;
    Context mContext;

    public AudioListAdapter(Context context, List<AudioModel> audioModelList) {

        mInflater = LayoutInflater.from(context);
        mContext = context;
        audioDataSet = audioModelList;
        itemsCopy = new ArrayList<>();
        itemsCopy.addAll(audioDataSet);
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View v = mInflater.inflate(R.layout.audio_list_row, viewGroup, false);

        AudioViewHolder vh = new AudioViewHolder(mContext,audioDataSet,v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder audioViewHolder, int i) {
        audioViewHolder.mTextView.setText(audioDataSet.get(i).getaName());
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
        notifyDataSetChanged();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context nContext;
        List<AudioModel> audioList;
        public TextView mTextView;

        public AudioViewHolder(Context context, List<AudioModel> audioModelList, View v) {
            super(v);
            nContext = context;
            audioList = audioModelList;
            mTextView = v.findViewById(R.id.audioName);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int itemPosition = getAdapterPosition();

            Intent intent = new Intent(nContext, MusicPlayerActivity.class);
            intent.putExtra("audio", audioList.get(itemPosition));

            nContext.startActivity(intent);

        }
    }
}