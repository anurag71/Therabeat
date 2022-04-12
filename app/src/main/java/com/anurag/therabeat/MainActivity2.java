package com.anurag.therabeat;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity2 extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener, SwipeRefreshLayout.OnRefreshListener {

    ConstraintLayout appBarLayout;
    Spinner spinner;
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
    View view;
    AppDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
    Calendar c = Calendar.getInstance();
    String date = sdf.format(c.getTime());
    // TODO: Rename and change types of parameters
    private RecyclerView myView;
    private SharedPreferences msharedPreferences;
    private String AUTH_TOKEN = "";
    private boolean isInitial = true;
    private boolean isPlaying = false;
    private String TAG = getClass().getSimpleName().toString();
    private SongService songService;
    private PlaylistService playlistService;
    String greeting;
    private static final String SHOWCASE_ID = "FirstTimeTutorial";
    boolean firstTime;
    View SearchMenuItem;
    View AnalyticsMenuItem;
    PowerSpinnerView spinnerView;

    PopupWindow popUp;
    boolean click = true;
    LinearLayout layout;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getInstance(this.getApplicationContext());
        msharedPreferences = SingletonInstances.getInstance(this.getApplicationContext()).getSharedPreferencesInstance();
        setContentView(R.layout.activity_main2);
// Specify the layout to use when the list of choices appears
//        FrameLayout cardView = findViewById(R.id.testFrame);
//        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame, appUsageFragment).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame1,new HomeFragment()).commit();
//        cardView.addView(appUsageFragment.getView());
        context = this;
        appBarLayout = findViewById(R.id.toolbarlayout);
        spinnerView=findViewById(R.id.cognitive_mode_selector);
        spinnerView.setIsFocusable(false);
        switch (msharedPreferences.getString("mode","Memory")){
            case "Memory":
                spinnerView.selectItemByIndex(0);
                break;
            case "Anxiety":
                spinnerView.selectItemByIndex(1);
                break;
            case "Attention":
                spinnerView.selectItemByIndex(2);
                break;
        }

        spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                switch (newIndex){
                    case 0:
                        appBarLayout.setBackgroundColor(R.color.light_green);
                        Toast.makeText(context,newItem + " selected!",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        appBarLayout.setBackgroundColor(R.color.light_blue);
                        Toast.makeText(context,newItem + " selected!",Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        appBarLayout.setBackgroundColor(R.color.light_red);
                        Toast.makeText(context,newItem + " selected!",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.actualtoolbar);
        toolbar.setTitle("Therabeat Playlist");

        setSupportActionBar(toolbar);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] a = {10800, 14400, 7200, 21600, 28800, 32400, 3600};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("hello", date + " totalusage " + a[j]);
                    db.totalUsageDao().insertTotalUsage(new TotalUsage(date, a[j]));
                }
            }
        });
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] b = {14400, 21600, 7200, 14400, 28800, 3600, 32400};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("hello", date + " memoryusage " + b[j]);
                    db.memoryUsageDao().insertMemoryUsage(new MemoryUsage(date, b[j]));
                }
            }
        });
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] c = {21600, 32400, 14400, 28800, 14400, 3600, 7200};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("hello", date + " anxietyusage " + c[j]);
                    db.anxietyUsageDao().insertAnxietyUsage(new AnxietyUsage(date, c[j]));
                }
            }
        });
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] d = {32400, 3600, 28800, 14400, 21600, 7200, 14400};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("date", date + " attentionusage " + d[j]);
                    db.attentionUsageDao().insertAttentionUsage(new AttentionUsage(date, d[j]));
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        msharedPreferences.edit().putFloat("beatFreq", 19.00F);
        msharedPreferences.edit().apply();
        wave = new Binaural(200, msharedPreferences.getFloat("beatFreq", 0.0F), 50);
        // popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
        mainLayout = findViewById(R.id.mainLayout);
        searchLayout = findViewById(R.id.searchFrame);
        SpotifyConnection spotifyConnection = new SpotifyConnection(this);
        SpotifyAppRemote mSpotifyAppRemote = spotifyConnection.mSpotifyAppRemote;
        spotifyConnection = new SpotifyConnection(this);
        songService = new SongService(this);
        playlistService = new PlaylistService(this);
        AUTH_TOKEN = msharedPreferences.getString("token", "");
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

//        floatingLayout.setOnMenuExpandedListener(new FloatingLayout.OnMenuExpandedListener() {
//            @Override
//            public void onMenuExpanded() {
//                floatingActionButton.setFabIcon(getDrawable(R.drawable.ic_round_add_24));
//                floatingActionButton.setText("");
//            }
//
//            @Override
//            public void onMenuCollapsed() {
//                floatingActionButton.setText("Switch Mode");
//                Drawable d = getResources().getDrawable(R.drawable.ic_round_close_24);
//                Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
//                transparentDrawable.setBounds(new Rect(0, 0, d.getMinimumWidth(), d.getMinimumHeight()));
//                floatingActionButton.setFabIcon(transparentDrawable);
//            }
//        });
//
//        AttentionActionButton.setOnClickListener(view -> {
//            msharedPreferences.edit().putFloat("beatFreq", 6.00F);
//            msharedPreferences.edit().apply();
//            Toast.makeText(this, "Switched Cognitive Mode to Attention", Toast.LENGTH_LONG).show();
//        });
//
//        AnxietyActionButton.setOnClickListener(view -> {
//            msharedPreferences.edit().putFloat("beatFreq", 4.00F);
//            msharedPreferences.edit().apply();
//            Toast.makeText(this, "Switched Cognitive Mode to Anxiety", Toast.LENGTH_SHORT).show();
//        });
//
//        MemoryActionButton.setOnClickListener(view -> {
//            msharedPreferences.edit().putFloat("beatFreq", 19.00F);
//            msharedPreferences.edit().apply();
//            Toast.makeText(this, "Switched Cognitive Mode to Memory", Toast.LENGTH_SHORT).show();
//        });
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
                firstTime = msharedPreferences.getBoolean("firstTime", true);
                if(firstTime){
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
                    searchFragment.setData(query);
                    Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    searchFragment.setData(newText);
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
            case R.id.aboutusitem:
                Intent intent;
                intent = new Intent(MainActivity2.this,

                        AboutUsActivity.class);
                startActivity(intent);
                return true;
            case R.id.feedbackitem:
                Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
                feedbackEmail.setType("text/email");
                feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"sampleutd@gmail.com"});
                feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Therabeat Feedback");
                startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
            case R.id.analytics:
//                MotionLayout tabContent = findViewById(R.id.RootLayout);
//
//                View overlay = findViewById(R.id.overlay);
                AppUsageFragment appUsageFragment = new AppUsageFragment();
                appUsageFragment.show(getSupportFragmentManager(),"analyticsfrag");

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            mainLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            searchView.onActionViewCollapsed();
        } else {
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setTitle("Quit Therabeat")
                    .setMessage("Are you sure you want to exit?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    }

    public void onRefresh() {

        // Fetching data from server
        playlistArrayList = songService.getPlaylistSongs(this.getApplicationContext(), listener, myView, mSwipeRefreshLayout, (TextView) findViewById(R.id.NoSongsTextView));
//        playlistService.getPlaylist(playlistImageView, this.getApplicationContext());
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
        msharedPreferences.edit().putBoolean("isPlaying", isPlaying).apply();
        // Subscribe to PlayerState

    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(spinnerView)
                        .setDismissText("GOT IT")
                        .setContentText("Switch between different cognitive modes and change Binaural Frequency. The default mode is Memory.")
                        .withOvalShape()
                        .build()
        );

        sequence.addSequenceItem(SearchMenuItem, "Search for your favorite songs and add them to your playlist for quicker access.", "GOT IT");
        sequence.addSequenceItem(AnalyticsMenuItem, "View your analytics for specific cognitive modes.", "GOT IT");

        sequence.start();
        msharedPreferences.edit().putBoolean("firstTime",false);
        msharedPreferences.edit().apply();

    }



}