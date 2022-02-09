package com.anurag.therabeat.connectors;

import android.content.Context;

public class RecyclerViewOnClickHandler implements RecyclerViewOnClick{

    Context context;

    RecyclerViewOnClickHandler(Context context){
        this.context = context;
    }

    @Override
    public void addToPlaylist(String uri) {
        PlaylistService playlistService = new PlaylistService(context);
        playlistService.addToPlaylist(uri, context.getApplicationContext());
    }

    @Override
    public void removeFromPlaylist(String uri) {

    }
}
