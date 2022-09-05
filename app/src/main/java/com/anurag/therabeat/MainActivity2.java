package com.anurag.therabeat;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SongService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity2 extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener, SwipeRefreshLayout.OnRefreshListener {


    ConstraintLayout appBarLayout;
    SearchView searchView;
    Context context;
    ConstraintLayout mainLayout;
    FrameLayout searchLayout;
    SearchFragment searchFragment;
    public static BeatsEngine wave;
    public ArrayList<Song> playlistArrayList = new ArrayList<>();
    SpotifyConnection spotifyConnection;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView playlistImageView;
    RecyclerViewAdapter.OnNoteListener listener;
    AppDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
    Calendar c = Calendar.getInstance();
    String date = sdf.format(c.getTime());
    // TODO: Rename and change types of parameters
    private RecyclerView myView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String AUTH_TOKEN = "";
    private boolean isInitial = true;
    private boolean isPlaying = false;
    private String TAG = getClass().getSimpleName().toString();
    private SongService songService;
    private PlaylistService playlistService;
    String greeting;
    private static final String SHOWCASE_ID = "FirstTimeSpotify";
    boolean firstTimeSpotify;
    View SearchMenuItem;
    View AnalyticsMenuItem;
    TextView CognitiveModeDisplay;

    FloatingLayout floatingLayout;
    FloatingActionButton floatingActionButton;
    FloatingActionButton AnxietyActionButton;
    FloatingActionButton AttentionActionButton;
    FloatingActionButton MemoryActionButton;

    PopupWindow popUp;
    boolean click = true;
    LinearLayout layout;
    boolean isConnected = false;
    NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build();
    ConnectivityManager connectivityManager;
    FrameLayout ErrorFrame;
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isConnected = true;
            runOnUiThread(() -> {
                ErrorFrame.setVisibility(View.GONE);
            });
            Log.d("Network State", "Network Connected");
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isConnected = false;
            Log.d("Network State", "Network Lost");
            runOnUiThread(() -> {
                ErrorFrame.setVisibility(View.VISIBLE);
            });
            Log.d("hello", String.valueOf(ErrorFrame.getVisibility()));
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        }
    };

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(this.getApplicationContext());
        mSharedPreferences = this.getSharedPreferences("Therabeat", 0);
        editor = this.getSharedPreferences("Therabeat", 0).edit();
        connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        setContentView(R.layout.activity_main2);
        ErrorFrame = findViewById(R.id.ErrorFrame);
// Specify the layout to use when the list of choices appears
//        FrameLayout cardView = findViewById(R.id.testFrame);
//        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame, appUsageFragment).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame1,new HomeFragment()).commit();
//        cardView.addView(appUsageFragment.getView());
        context = this;
        appBarLayout = findViewById(R.id.toolbarlayout);
        CognitiveModeDisplay = findViewById(R.id.CognitiveModeDisplay);
        floatingLayout = findViewById(R.id.floating_layout);
        String mode = mSharedPreferences.getString("mode", "Memory");
        CognitiveModeDisplay.setText(mode);
        switch (mode) {
            case "Memory":
                appBarLayout.setBackgroundColor(Color.parseColor("#c8e6c9"));
                getWindow().setStatusBarColor(Color.parseColor("#c8e6c9"));
                break;
            case "Anxiety":
                appBarLayout.setBackgroundColor(Color.parseColor("#bbdefb"));
                getWindow().setStatusBarColor(Color.parseColor("#bbdefb"));
                break;
            case "Attention":
                appBarLayout.setBackgroundColor(Color.parseColor("#ffcdd2"));
                getWindow().setStatusBarColor(Color.parseColor("#ffcdd2"));
                break;
        }

        Toolbar toolbar = findViewById(R.id.actualtoolbar);
        toolbar.setTitle("Therabeat");

        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager.requestNetwork(networkRequest, networkCallback);
        editor.putFloat("beatFreq", 19.00F);
        editor.apply();
        wave = new Binaural(200, mSharedPreferences.getFloat("beatFreq", 0.0F), 50);
        // popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
        mainLayout = findViewById(R.id.mainLayout);
        searchLayout = findViewById(R.id.searchFrame);
        SpotifyConnection spotifyConnection = new SpotifyConnection(this);
        SpotifyAppRemote mSpotifyAppRemote = spotifyConnection.mSpotifyAppRemote;
        spotifyConnection = new SpotifyConnection(this);
        songService = new SongService(this);
        playlistService = new PlaylistService(this);
        AUTH_TOKEN = mSharedPreferences.getString("token", "");
        listener = this;
        playlistImageView = findViewById(R.id.playlistArtwork);
        myView = (RecyclerView) findViewById(R.id.playlistsongrecyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        myView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                SearchMenuItem = findViewById(R.id.action_search);
                AnalyticsMenuItem = findViewById(R.id.analytics);
                firstTimeSpotify = mSharedPreferences.getBoolean("firstTimeSpotify", true);
                if (firstTimeSpotify) {
                    presentShowcaseSequence();
                }

            }
        });
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    mainLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    searchView.onActionViewCollapsed();
                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                    mainLayout.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                    searchFragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.searchFrame, searchFragment).commit();
                }
            });
            EditText searchPlate = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchPlate.setHint("Enter song name");
            View searchPlateView = searchView.findViewById(R.id.search_plate);
//            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    if (isConnected) {
                        searchFragment.setData(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    if (isConnected) {
                        searchFragment.setData(newText);
                    }
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.feedbackitem:
                Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
                feedbackEmail.setType("text/email");
                feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"sampleutd@gmail.com"});
                feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Therabeat Feedback");
                startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
                return true;
            case R.id.analytics:
//                MotionLayout tabContent = findViewById(R.id.RootLayout);
//
//                View overlay = findViewById(R.id.overlay);
                AppUsageFragment appUsageFragment = new AppUsageFragment();
                appUsageFragment.show(getSupportFragmentManager(), "analyticsfrag");
                return true;
            case R.id.settingsitem:
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("settings")
                        .replace(R.id.SpotifySettingsFragment, new Settings())
                        .commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            if (!searchView.isIconified()) {
                mainLayout.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
                searchView.onActionViewCollapsed();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void onRefresh() {

        // Fetching data from server
        playlistArrayList = songService.getPlaylistSongs(this.getApplicationContext(), listener, myView, mSwipeRefreshLayout, (TextView) findViewById(R.id.NoSongsTextView));
//        playlistService.getPlaylist(playlistImageView, this.getApplicationContext());
    }

    public void onNoteClick(int position) {
        float beatFreq = mSharedPreferences.getFloat("beatFreq", 0.0f);
        Log.d(TAG, playlistArrayList.get(position).getName());
        if (isConnected) {
            togglePlay(beatFreq, playlistArrayList.get(position).getUri());
        }
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
                    usage = usage + ((System.currentTimeMillis() / 1000) - mSharedPreferences.getLong("startTime", (long) 0.0));
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
                        usage = usage + ((System.currentTimeMillis() / 1000) - mSharedPreferences.getLong("startTime", (long) 0.0));
                        db.memoryUsageDao().insertMemoryUsage(new MemoryUsage(date, usage.intValue()));
                    } else if (beatFreq == 6.00) {
                        AttentionUsage attention;
                        attention = db.attentionUsageDao().getAttentionUsageByDate(date);
                        if (attention == null) {
                            db.attentionUsageDao().insertAttentionUsage(new AttentionUsage(date, 0));
                        }
                        attention = db.attentionUsageDao().getAttentionUsageByDate(date);
                        Long usage = Long.valueOf(attention.getTimeUsed());
                        usage = usage + ((System.currentTimeMillis() / 1000) - mSharedPreferences.getLong("startTime", (long) 0.0));
                        db.attentionUsageDao().insertAttentionUsage(new AttentionUsage(date, usage.intValue()));
                    } else {
                        AnxietyUsage anxiety;
                        anxiety = db.anxietyUsageDao().getAnxietyUsageByDate(date);
                        if (anxiety == null) {
                            db.anxietyUsageDao().insertAnxietyUsage(new AnxietyUsage(date, 0));
                        }
                        anxiety = db.anxietyUsageDao().getAnxietyUsageByDate(date);
                        Long usage = Long.valueOf(anxiety.getTimeUsed());
                        usage = usage + ((System.currentTimeMillis() / 1000) - mSharedPreferences.getLong("startTime", (long) 0.0));
                        db.anxietyUsageDao().insertAnxietyUsage(new AnxietyUsage(date, usage.intValue()));
                    }
                }
            });
//            appUsageDao.insert(new AppUsageHistory(date,));
        }


        Log.d(TAG, String.valueOf(beatFreq));
        wave.start();
        Long start = System.currentTimeMillis() / 1000;
        editor.putLong("startTime", start).apply();
        getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment) (new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
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
                alertDialog = new AlertDialog.Builder(this);
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
        editor.putBoolean("isPlaying", isPlaying).apply();
        // Subscribe to PlayerState

    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(CognitiveModeDisplay)
                        .setDismissText("GOT IT")
                        .setContentText("Current cognitive mode is displayed here")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(SearchMenuItem, "Search for your favorite songs and add them to your playlist for quicker access.", "GOT IT");
        sequence.addSequenceItem(AnalyticsMenuItem, "View your analytics for specific cognitive modes.", "GOT IT");

        sequence.start();
        editor.putBoolean("firstTimeSpotify", false);
        editor.apply();

    }

    private void ChangeMode(int newIndex) {
        switch (newIndex) {
            case 0:
                appBarLayout.setBackgroundColor(Color.parseColor("#c8e6c9"));
                getWindow().setStatusBarColor(Color.parseColor("#c8e6c9"));
                editor.putFloat("beatFreq", 19.00F);
                editor.putString("mode", "Memory");
                editor.apply();
                break;
            case 1:
                appBarLayout.setBackgroundColor(Color.parseColor("#bbdefb"));
                getWindow().setStatusBarColor(Color.parseColor("#bbdefb"));
                editor.putFloat("beatFreq", 4.00F);
                editor.putString("mode", "Anxiety");
                editor.apply();
                break;
            case 2:
                appBarLayout.setBackgroundColor(Color.parseColor("#ffcdd2"));
                getWindow().setStatusBarColor(Color.parseColor("#ffcdd2"));
                editor.putFloat("beatFreq", 6.00F);
                editor.putString("mode", "Attention");
                editor.apply();
                break;
        }
    }


}