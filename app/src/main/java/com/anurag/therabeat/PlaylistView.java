package com.anurag.therabeat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistView extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ArrayList<String> playlists;
    SharedPreferences mSharedPreferences;
    RecyclerView DisplayPlaylist;
    DisplayPlayListAdaptor adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlaylistView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistView.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistView newInstance(String param1, String param2) {
        PlaylistView fragment = new PlaylistView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mSharedPreferences = getContext().getSharedPreferences("Therabeat", 0);
        String tempList = mSharedPreferences.getString("Playlists", "");
        if (tempList.equals("")) {
            playlists = new ArrayList<>();
        } else {
            TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {
            };
            Gson gson = new Gson();
            playlists = gson.fromJson(tempList, token.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_playlist_view, container, false);
        DisplayPlaylist = inflatedView.findViewById(R.id.PlayListNameRV);
        DisplayPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DisplayPlayListAdaptor(playlists, getContext());
        DisplayPlaylist.setAdapter(adapter);
        return inflatedView;
    }
}

class DisplayPlayListAdaptor extends RecyclerView.Adapter<DisplayPlayListAdaptor.ViewHolder> {

    private ArrayList<String> localDataSet;
    private Context context;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public DisplayPlayListAdaptor(ArrayList<String> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_row, viewGroup, false);

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
            textView = (TextView) view.findViewById(R.id.audioName);
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
                Bundle bundle = new Bundle();
                bundle.putString("PlaylistName", textView.getText().toString());
                bundle.putSerializable("playlistSongs", songs);
                ModalPlaylistSongsSheet modalPlaylistSongsSheet = new ModalPlaylistSongsSheet();
                modalPlaylistSongsSheet.setArguments(bundle);
                AppCompatActivity activity = (MusicListActivity) context;
                modalPlaylistSongsSheet.show(activity.getSupportFragmentManager(), ModalPlaylistSongsSheet.TAG);
            });

            // Define click listener for the ViewHolder's View


        }

        public TextView getTextView() {
            return textView;
        }
    }
}