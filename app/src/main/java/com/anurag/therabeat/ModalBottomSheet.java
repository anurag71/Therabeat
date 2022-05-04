package com.anurag.therabeat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ModalBottomSheet extends BottomSheetDialogFragment {

    public static String TAG = "ModalBottomSheet";
    static AudioModel song;
    static BottomSheetBehavior bottomSheetBehavior;
    ArrayList<String> playlists;
    MaterialButton CreatePlaylistButton;
    View CreatePlaylistDialogView;
    SharedPreferences mSharedPreferences;
    AddToModalSheetAdaptor adapter;
    RecyclerView DisplayPlaylists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false);
        FrameLayout bottomSheet = view.findViewById(R.id.standard_bottom_sheet);
        mSharedPreferences = getContext().getSharedPreferences("Therabeat", 0);
        song = (AudioModel) getArguments().getSerializable("song");
        String tempList = mSharedPreferences.getString("Playlists", "");
        if (tempList.equals("")) {
            playlists = new ArrayList<>();
        } else {
            TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {
            };
            Gson gson = new Gson();
            playlists = gson.fromJson(tempList, token.getType());
        }
        Log.d(TAG, playlists.toString());
        CreatePlaylistButton = view.findViewById(R.id.CreatePlaylist);
        DisplayPlaylists = view.findViewById(R.id.PlaylistSongRV);
        DisplayPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AddToModalSheetAdaptor(playlists, getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DisplayPlaylists.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        DisplayPlaylists.addItemDecoration(dividerItemDecoration);
        DisplayPlaylists.setAdapter(adapter);
        CreatePlaylistButton.setOnClickListener(view1 -> {
            CreatePlaylistDialogView = LayoutInflater.from(getContext()).inflate(R.layout.create_playlist_dialog, null, false);
            buildAlertDialog();
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

    private void buildAlertDialog() {
        final String[] str = {""};
        DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Gson gson = new Gson();
                EditText PlaylistNameEditText = CreatePlaylistDialogView.findViewById(R.id.PlaylistName);
                String PlaylistName = PlaylistNameEditText.getText().toString();
                if (TextUtils.isEmpty(PlaylistNameEditText.getText())) {
                    Toast.makeText(getContext(), "Invalid Playlist Name", Toast.LENGTH_SHORT).show();
                } else {
                    playlists.add(PlaylistName);
                    mSharedPreferences.edit().putString("Playlist" + PlaylistName, "");
                    String json = gson.toJson(playlists);
                    mSharedPreferences.edit().putString("Playlists", json).apply();
                    adapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                }
            }
        };
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
        dialog
                .setView(CreatePlaylistDialogView)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Create", positiveButton)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}

class AddToModalSheetAdaptor extends RecyclerView.Adapter<AddToModalSheetAdaptor.ViewHolder> {

    private ArrayList<String> localDataSet;
    private Context context;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public AddToModalSheetAdaptor(ArrayList<String> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.add_playlist_modal_row_item, viewGroup, false);

        return new ViewHolder(view, context);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view, Context context) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textview);
            view.setOnClickListener(view1 -> {
                Gson gson = new Gson();
                ArrayList<AudioModel> songs;
                String tempList = context.getSharedPreferences("Therabeat", 0).getString("Playlist" + textView.getText(), "");
                if (tempList.equals("")) {
                    songs = new ArrayList<>();
                } else {
                    TypeToken<ArrayList<AudioModel>> token = new TypeToken<ArrayList<AudioModel>>() {
                    };
                    songs = gson.fromJson(tempList, token.getType());
                }
                Log.d(ModalBottomSheet.TAG, songs.toString());
                if (!songs.contains(ModalBottomSheet.song)) {
                    songs.add(ModalBottomSheet.song);
                    String json = gson.toJson(songs);
                    context.getSharedPreferences("Therabeat", 0).edit().putString("Playlist" + textView.getText(), json).apply();
                    Toast.makeText(context, "Added song to playlist", Toast.LENGTH_SHORT).show();
                    AppCompatActivity activity = (MusicListActivity) context;
                    Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(ModalBottomSheet.TAG);
                    activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                } else {
                    Toast.makeText(context, "Song is already present", Toast.LENGTH_SHORT).show();
                }
            });

            // Define click listener for the ViewHolder's View


        }

        public TextView getTextView() {
            return textView;
        }
    }
}
