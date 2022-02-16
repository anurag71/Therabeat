package com.anurag.therabeat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SongService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements RecyclerViewAdapter.OnNoteListener, NavigationBarView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static BeatsEngine wave;
    public ArrayList<Song> playlistArrayList = new ArrayList<>();
    SpotifyConnection spotifyConnection;
    // TODO: Rename and change types of parameters
    private RecyclerView myView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView playlistImageView;
    private SharedPreferences msharedPreferences;
    private String AUTH_TOKEN = "";
    private boolean isInitial = true;
    private boolean isPlaying = false;
    private String TAG = getClass().getSimpleName().toString();
    RecyclerViewAdapter.OnNoteListener listener;
    View view;
    private SongService songService;
    private PlaylistService playlistService;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        wave = MainActivity.wave;
        super.onCreate(savedInstanceState);
        spotifyConnection = new SpotifyConnection(getActivity());
        msharedPreferences = getActivity().getSharedPreferences("Therebeat", 0);
        AUTH_TOKEN = msharedPreferences.getString("token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        songService = new SongService(getActivity());
        playlistService = new PlaylistService(getActivity());

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        listener = this;
        super.onViewCreated(view, savedInstanceState);
        playlistImageView = view.findViewById(R.id.playlistArtwork);
        myView = (RecyclerView) view.findViewById(R.id.playlistsongrecyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        myView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                playlistArrayList = songService.getPlaylistSongs(getActivity().getApplicationContext(), listener, myView, mSwipeRefreshLayout, (TextView) view.findViewById(R.id.textView1));
                playlistService.getPlaylist(playlistImageView, getActivity().getApplicationContext());
            }
        });


    }

    public void onRefresh() {

        // Fetching data from server
        playlistArrayList = songService.getPlaylistSongs(getActivity().getApplicationContext(), listener, myView, mSwipeRefreshLayout, (TextView) view.findViewById(R.id.textView1));
        playlistService.getPlaylist(playlistImageView, getActivity().getApplicationContext());
    }

    public void onNoteClick(int position) {
        float beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);
        Log.d(TAG, playlistArrayList.get(position).getName());
        togglePlay(beatFreq, playlistArrayList.get(position).getUri());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment) (new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
    }

    private void attemptStartWave(float beatFreq) {
        if (wave.getIsPlaying()) {
            msharedPreferences.edit().putLong("timeListened", msharedPreferences.getLong("timeListened", (long) 0.0) + (System.currentTimeMillis() / 1000 - msharedPreferences.getLong("startTime", (long) 0.0))).apply();
        }
        Log.d(TAG, String.valueOf(beatFreq));
        wave.start();
        msharedPreferences.edit().putLong("startTime", System.currentTimeMillis() / 1000).apply();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void togglePlay(float beatFreq, String playlistId) {
        connected(playlistId);
        attemptStartWave(beatFreq);
    }

    private void connected(String uri) {
        spotifyConnection.mSpotifyAppRemote.getPlayerApi().play(uri);
        spotifyConnection.mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d(TAG, track.name + " by " + track.artist.name);
                    }
                });
        isPlaying = true;
        // Subscribe to PlayerState

    }
}