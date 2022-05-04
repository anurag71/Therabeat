package com.anurag.therabeat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.anurag.therabeat.connectors.PlaylistService;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private List<AudioModel> audioDataSet;
    private LayoutInflater mInflater;
    private List<AudioModel> itemsCopy;
    int ViewPagerPosition;
    Context mContext;
    private ItemClickListener mListener;
    boolean AddToPlaylist;

    public AudioListAdapter(Context context, List<AudioModel> audioModelList, ItemClickListener listener, int ViewPagerPosition, boolean AddToPlaylist) {

        mInflater = LayoutInflater.from(context);
        mContext = context;
        audioDataSet = audioModelList;
        itemsCopy = new ArrayList<>();
        itemsCopy.addAll(audioDataSet);
        this.mListener = listener;
        this.ViewPagerPosition = ViewPagerPosition;
        this.AddToPlaylist = AddToPlaylist;
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
            audioViewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(view.getContext(), audioViewHolder.buttonViewOption);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.offline_audio_list_menu);
                    if (!AddToPlaylist) {
                        popup.getMenu().removeItem(1);
                    }
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            PlaylistService playlistService = new PlaylistService(audioViewHolder.buttonViewOption.getContext());
                            switch (item.getItemId()) {
                                case R.id.AddToQueue:
                                    ExoPlayer exoPlayer = SingletonInstances.getInstance(mContext.getApplicationContext()).getExoPlayer();

                                    MediaItem NextSong = new MediaItem.Builder()
                                            .setUri(Uri.fromFile(new File(audioDataSet.get(audioViewHolder.getAbsoluteAdapterPosition()).getaPath())))
                                            .setTag(audioDataSet.get(audioViewHolder.getAbsoluteAdapterPosition()))
                                            .build();

                                    exoPlayer.addMediaItem(NextSong);
                                    Toast.makeText(mContext.getApplicationContext(), "Added to queue", Toast.LENGTH_SHORT).show();
                                    //handle menu1 click
//                                    SingletonInstances
//                                    playlistService.addToPlaylist(audioDataSet.get(audioViewHolder.getAdapterPosition())., audioViewHolder.buttonViewOption.getContext().getApplicationContext());
                                    return true;
                                case R.id.AddToPlaylist:
                                    Bundle song = new Bundle();
                                    song.putSerializable("song", audioDataSet.get(audioViewHolder.getAbsoluteAdapterPosition()));
                                    ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
                                    modalBottomSheet.setArguments(song);
                                    AppCompatActivity activity = (MusicListActivity) mContext;
                                    modalBottomSheet.show(activity.getSupportFragmentManager(), ModalBottomSheet.TAG);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
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
        private TextView buttonViewOption;
        private ItemClickListener mListener;

        public AudioViewHolder(Context context, List<AudioModel> audioModelList, View v, ItemClickListener mListener) {
            super(v);
            nContext = context;
            audioList = audioModelList;
            mTextView = v.findViewById(R.id.audioName);
            mCheckBox = v.findViewById(R.id.favorite);
            buttonViewOption = v.findViewById(R.id.textViewOptions);
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