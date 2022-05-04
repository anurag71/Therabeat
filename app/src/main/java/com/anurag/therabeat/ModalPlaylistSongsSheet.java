package com.anurag.therabeat;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModalPlaylistSongsSheet extends BottomSheetDialogFragment implements AudioListAdapter.ItemClickListener {

    public static String TAG = "ModalPlaylistSongsSheet";
    static BottomSheetBehavior bottomSheetBehavior;
    MaterialButton PlayPlaylist;
    View CreatePlaylistDialogView;
    RecyclerView DisplayPlaylist;
    AudioListAdapter adapter;
    List<AudioModel> songs = new ArrayList<>();
    ExoPlayer exoPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        exoPlayer = SingletonInstances.getInstance(getContext().getApplicationContext()).getExoPlayer();
        View view = inflater.inflate(R.layout.modal_playlist_songs_sheet, container, false);
        FrameLayout bottomSheet = view.findViewById(R.id.standard_bottom_sheet);
        TextView textView = (TextView) view.findViewById(R.id.sample1);
        textView.setText(getArguments().getString("PlaylistName"));
        DisplayPlaylist = view.findViewById(R.id.PlaylistNamesRecyclerView);
        PlayPlaylist = view.findViewById(R.id.play);
        DisplayPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        songs = (ArrayList<AudioModel>) getArguments().getSerializable("playlistSongs");
        adapter = new AudioListAdapter(getContext(), songs, this, 0, false);
        DisplayPlaylist.setAdapter(adapter);

        PlayPlaylist.setOnClickListener(view1 -> {

            exoPlayer.clearMediaItems();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.offline_play_screen_frame_layout, (Fragment) BlankFragment.newInstance(songs.get(0))).setReorderingAllowed(true).commitAllowingStateLoss();
            List<MediaItem> temp = new ArrayList<>();
            Log.d("size", String.valueOf(songs.size()));
            for (int i = 0; i < songs.size(); i++) {
                Log.d(TAG, "insdie");
                MediaItem NextSong = new MediaItem.Builder()
                        .setUri(Uri.fromFile(new File(songs.get(i).getaPath())))
                        .setTag(songs.get(i))
                        .build();

                exoPlayer.addMediaItem(NextSong);
                if (exoPlayer.hasNextMediaItem()) {
                    Log.d("media", "yes");
                }
            }
            dismiss();
        });

        view.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view.getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.standard_bottom_sheet);
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setFitToContents(false);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setDraggable(true);
                coordinatorLayout.getParent().requestLayout();
            }
        });
    }

    @Override
    public void onItemClicked(AudioModel audio) {
        Log.d("name", audio.getaName());
        exoPlayer.clearMediaItems();
        MediaItem NextSong = new MediaItem.Builder()
                .setUri(Uri.fromFile(new File(audio.getaPath())))
                .setTag(audio)
                .build();

        exoPlayer.addMediaItem(NextSong);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.offline_play_screen_frame_layout, (Fragment) BlankFragment.newInstance(audio)).setReorderingAllowed(true).commitAllowingStateLoss();
        dismiss();
    }

//    private void buildAlertDialog() {
//        final String[] str = {""};
//        DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Gson gson = new Gson();
//                EditText PlaylistNameEditText = CreatePlaylistDialogView.findViewById(R.id.PlaylistName);
//                String PlaylistName = PlaylistNameEditText.getText().toString();
//                if(TextUtils.isEmpty(PlaylistNameEditText.getText())){
//                    Toast.makeText(getContext(),"Invalid Playlist Name",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    playlists.add(PlaylistName);
//                    mSharedPreferences.edit().putString("Playlist"+PlaylistName, "");
//                    String json = gson.toJson(playlists);
//                    mSharedPreferences.edit().putString("Playlists", json).apply();
//                    adapter.notifyDataSetChanged();
//                    dialogInterface.dismiss();
//                }
//            }
//        };
//        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
//        dialog
//                .setView(CreatePlaylistDialogView)
//
//                // Specifying a listener allows you to take an action before dismissing the dialog.
//                // The dialog is automatically dismissed when a dialog button is clicked.
//                .setPositiveButton("Create", positiveButton)
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .show();
//    }
}
