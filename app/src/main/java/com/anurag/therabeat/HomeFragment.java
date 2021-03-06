package com.anurag.therabeat;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anurag.therabeat.Database.AnxietyUsage;
import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AppExecutors;
import com.anurag.therabeat.Database.AttentionUsage;
import com.anurag.therabeat.Database.MemoryUsage;
import com.anurag.therabeat.Database.TotalUsage;
import com.anurag.therabeat.Database.TotalUsageDao;
import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SongService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.protocol.types.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
    Calendar c = Calendar.getInstance();
    String date = sdf.format(c.getTime());

    TotalUsageDao appUsageDao;
    AppDatabase db;

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
//        appUsageDao = SingletonInstances.getInstance(getActivity().getApplicationContext()).getDbInstance().appUsageDao();
        Log.d("date", date);
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
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(getActivity().getApplicationContext());

        msharedPreferences = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance();
        AUTH_TOKEN = msharedPreferences.getString("token", "");
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
        mSwipeRefreshLayout.setNestedScrollingEnabled(true);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                onRefresh();

            }
        });

        myView.addOnScrollListener(new RecyclerView.OnScrollListener() { //Used to restrict collapsing of recycler view horizontal swipe and swipe refresh layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });


    }


    public void onRefresh() {

        // Fetching data from server
        playlistArrayList = songService.getPlaylistSongs(getActivity().getApplicationContext(), listener, myView, mSwipeRefreshLayout, (TextView) view.findViewById(R.id.textView1), (TextView) view.findViewById(R.id.playlistName), playlistImageView);
        playlistService.getPlaylist(playlistImageView, getActivity().getApplicationContext());
    }

    public void onNoteClick(int position) {
        float beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);
        Log.d(TAG, playlistArrayList.get(position).getName());
        togglePlay(beatFreq, playlistArrayList.get(position).getUri());
    }

    private void attemptStartWave(float beatFreq) {
        if (wave.getIsPlaying()) {
//            Long usage = appUsageDao.fetchTimeForDate(date).getTimeUsed();

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    TotalUsage p = db.totalUsageDao().getTotalUsageByDate(date);
                    if (p == null) {
                        db.totalUsageDao().insertTotalUsage(new TotalUsage(date, 0));
                    }
                    p = db.totalUsageDao().getTotalUsageByDate(date);
                    Long usage = Long.valueOf(p.getTimeUsed());
                    usage = usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0));
                    db.totalUsageDao().insertTotalUsage(new TotalUsage(date, usage.intValue()));
                }
            });
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (beatFreq == 19.0) {
                        MemoryUsage m;
                        m = db.memoryUsageDao().getMemoryUsageByDate(date);
                        if (m == null) {
                            db.memoryUsageDao().insertMemoryUsage(new MemoryUsage(date, 0));
                        }
                        m = db.memoryUsageDao().getMemoryUsageByDate(date);
                        Long usage = Long.valueOf(m.getTimeUsed());
                        usage = usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0));
                        db.memoryUsageDao().insertMemoryUsage(new MemoryUsage(date, usage.intValue()));
                    } else if (beatFreq == 6.00) {
                        AttentionUsage attention;
                        attention = db.attentionUsageDao().getAttentionUsageByDate(date);
                        if (attention == null) {
                            db.attentionUsageDao().insertAttentionUsage(new AttentionUsage(date, 0));
                        }
                        attention = db.attentionUsageDao().getAttentionUsageByDate(date);
                        Long usage = Long.valueOf(attention.getTimeUsed());
                        usage = usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0));
                        db.attentionUsageDao().insertAttentionUsage(new AttentionUsage(date, usage.intValue()));
                    } else {
                        AnxietyUsage anxiety;
                        anxiety = db.anxietyUsageDao().getAnxietyUsageByDate(date);
                        if (anxiety == null) {
                            db.anxietyUsageDao().insertAnxietyUsage(new AnxietyUsage(date, 0));
                        }
                        anxiety = db.anxietyUsageDao().getAnxietyUsageByDate(date);
                        Long usage = Long.valueOf(anxiety.getTimeUsed());
                        usage = usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0));
                        db.anxietyUsageDao().insertAnxietyUsage(new AnxietyUsage(date, usage.intValue()));
                    }
                }
            });
//            appUsageDao.insert(new AppUsageHistory(date,));
        }


        Log.d(TAG, String.valueOf(beatFreq));
        wave.start();
        Long start = System.currentTimeMillis() / 1000;
        msharedPreferences.edit().putLong("startTime", start).apply();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment) (new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void togglePlay(float beatFreq, String playlistId) {
        attemptStartWave(beatFreq);
        connected(playlistId);
    }

    private void connected(String uri) {
        spotifyConnection.mSpotifyAppRemote.getUserApi().getCapabilities().setResultCallback(data -> {
            if (!data.canPlayOnDemand) {
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog
                        .setTitle("Information")
                        .setMessage("Spotify Premium lets you play any track, ad-free and with better audio quality. Go to spotify.com/premium to try it for free.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });
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
        msharedPreferences.edit().putBoolean("isPlaying", isPlaying).apply();
        // Subscribe to PlayerState

    }
}