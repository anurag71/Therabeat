package com.anurag.therabeat.connectors;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.anurag.therabeat.Song;

import java.util.List;

public class SongListDiffCallback extends DiffUtil.Callback {

    private final List<Song> mOldSongList;
    private final List<Song> mNewSongList;

    public SongListDiffCallback(List<Song> oldSongList, List<Song> newSongList) {
        this.mOldSongList = oldSongList;
        this.mNewSongList = newSongList;
    }

    @Override
    public int getOldListSize() {
        return mOldSongList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewSongList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldSongList.get(oldItemPosition).getName() == mNewSongList.get(
                newItemPosition).getName();
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Song oldSong = mOldSongList.get(oldItemPosition);
        final Song newSong = mNewSongList.get(newItemPosition);

        return oldSong.getName().equals(newSong.getName());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}