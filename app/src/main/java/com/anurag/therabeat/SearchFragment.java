package com.anurag.therabeat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AppExecutors;
import com.anurag.therabeat.Database.Person;
import com.anurag.therabeat.Database.PersonDao;
import com.anurag.therabeat.connectors.SongService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.protocol.types.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    private SongService playlistService;
    private String AUTH_TOKEN = "";
    //Refresh waveform if user changes frequencies
    private String TAG = getClass().getSimpleName().toString();
    private boolean isInitial = true;
    private boolean isPlaying = false;

    private SharedPreferences.Editor editor;
    RecyclerViewAdapter adapter;
    private SharedPreferences msharedPreferences;
    private boolean exception = false;
    private String errorMsg = "";
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
    Calendar c = Calendar.getInstance();
    String date = sdf.format(c.getTime());
    AppDatabase db;


    PersonDao appUsageDao;


    public SearchFragment() {
        // Required empty public constructor
    }

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
        wave = MainActivity.wave;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_search, container, false);
//        appUsageDao = SingletonInstances.getInstance(getActivity().getApplicationContext()).getDbInstance().appUsageDao();
        spotifyConnection = new SpotifyConnection(getActivity());
        msharedPreferences = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance();
        AUTH_TOKEN = msharedPreferences.getString("token", "");
        Log.d(TAG, AUTH_TOKEN);
//		waitForUserInfo();
        playlistService = new SongService(getActivity());
        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(getActivity().getApplicationContext());
        searchEditText = (EditText) view.findViewById(R.id.songSearch);
        myView = (RecyclerView) view.findViewById(R.id.recyclerview);
        myView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


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
    }

    private void getPlaylists(String searchQuery) {
        Log.d(TAG, "inside get playlist");
        adapter = new RecyclerViewAdapter(playlistArrayList, this, R.menu.recycler_view_options_menu);
        myView.setAdapter(adapter);
        playlistArrayList = playlistService.searchSongs(getActivity().getApplicationContext(), searchQuery, this, adapter);


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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onNoteClick(int position) {
        float beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);
        Log.d(TAG, playlistArrayList.get(position).getName());
        togglePlay(beatFreq, playlistArrayList.get(position).getUri());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment) (new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
    }

    private void attemptStartWave(float beatFreq) {
        if (wave.getIsPlaying()) {
//            Long usage = appUsageDao.fetchTimeForDate(date).getTimeUsed();
//            appUsageDao.insert(new AppUsageHistory(date,usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0))));
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Person p = db.personDao().loadPersonById(date);
                    if (p == null) {
                        db.personDao().insertPerson(new Person(date, 0));
                    }
                    p = db.personDao().loadPersonById(date);
                    Long usage = Long.valueOf(p.getTimeUsed());
                    usage = usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0));
                    db.personDao().insertPerson(new Person(date, usage.intValue()));
                }
            });
        }
        Log.d(TAG, String.valueOf(beatFreq));
        wave.start();
        Long start = System.currentTimeMillis() / 1000;
        msharedPreferences.edit().putLong("startTime", start).apply();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void togglePlay(float beatFreq, String playlistId) {
        attemptStartWave(beatFreq);
        connected(playlistId);
    }
}