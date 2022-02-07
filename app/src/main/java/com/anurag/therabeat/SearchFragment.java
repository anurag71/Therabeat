package com.anurag.therabeat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements RecyclerViewAdapter.OnNoteListener, NavigationBarView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static BeatsEngine wave;
    // TODO: Rename and change types of parameters
    public ArrayList<Song> playlistArrayList = new ArrayList<>();
    protected EditText searchEditText;
    //Spotify
    SpotifyConnection spotifyConnection;
    int position;
    private RecyclerView myView;
    private PlaylistService playlistService;
    private RequestQueue queue;
    private String AUTH_TOKEN = "";
    //Refresh waveform if user changes frequencies
    private String TAG = getClass().getSimpleName().toString();
    private boolean isInitial = true;
    private boolean isPlaying = false;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private boolean exception = false;
    private String errorMsg = "";

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_video, container, false);
        spotifyConnection = new SpotifyConnection(getContext());
        msharedPreferences = getContext().getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(getContext());
        AUTH_TOKEN = msharedPreferences.getString("token", "");
        Log.d(TAG, AUTH_TOKEN);
//		waitForUserInfo();
        playlistService = new PlaylistService(getContext());
        searchEditText = inflatedView.findViewById(R.id.songSearch);
        myView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0)
                    getPlaylists(s.toString());
            }
        });
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    private void getPlaylists(String searchQuery) {
        updatePlaylist();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(playlistArrayList, this);
        myView.setAdapter(adapter);
        playlistArrayList = playlistService.getPlaylists(getContext(), searchQuery, this, adapter);

    }

    public void updatePlaylist() {
        Log.d(TAG, "inside update playlsit");

        myView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);
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

    public void onNoteClick(int position) {
        float beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);
        Log.d(TAG, playlistArrayList.get(position).getName());
        togglePlay(beatFreq, playlistArrayList.get(position).getUri());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment) (new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
    }

    private void attemptStartWave(float beatFreq) {
        Log.d(TAG, String.valueOf(beatFreq));
        wave = new Binaural(200, beatFreq, 50);
        wave.start();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void togglePlay(float beatFreq, String playlistId) {
        connected(playlistId);
        attemptStartWave(beatFreq);
    }
}