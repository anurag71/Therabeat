package com.anurag.therabeat;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MusicListActivity extends AppCompatActivity {

    private static final String SHOWCASE_ID = "FirstTimeOffline";

    Context context;
    public static List<AudioModel> allAudioFiles;
    public static List<AudioModel> artistList = new ArrayList<>();
    public static List<AudioModel> albumList = new ArrayList<>();

    public static List<AudioModel> FavList;
    public static List<Integer> FavListIds;
    static AudioListAdapter adapter;
    static AudioListAdapter FavAdaptor;
    static RecyclerView FavRecyclerView;
    ConstraintLayout appBarLayout;
    PowerSpinnerView spinnerView;
    SearchView searchView;
    ConstraintLayout mainLayout;
    FrameLayout searchLayout;
    SearchFragment searchFragment;
    boolean firstTimeOffline;
    View SearchMenuItem;
    View AnalyticsMenuItem;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String AUTH_TOKEN = "";
    private boolean isInitial = true;
    private boolean isPlaying = false;
    private String TAG = getClass().getSimpleName().toString();

    FloatingLayout floatingLayout;
    FloatingActionButton floatingActionButton;
    FloatingActionButton AnxietyActionButton;
    FloatingActionButton AttentionActionButton;
    FloatingActionButton MemoryActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);


        //Checks permissions

        //Finds all the mp3 files in device and makes a list
//        getSongs();
        mSharedPreferences = this.getSharedPreferences("Therabeat", 0);
        String FList = mSharedPreferences.getString("FavList", "");
        String FListIds = mSharedPreferences.getString("FavListIds", "");
        if (FList.equals("")) {
            FavList = new ArrayList<>();
            FavListIds = new ArrayList<>();
        } else {
            TypeToken<ArrayList<AudioModel>> favList = new TypeToken<ArrayList<AudioModel>>() {
            };
            TypeToken<ArrayList<Integer>> favListIds = new TypeToken<ArrayList<Integer>>() {
            };
            Gson gson = new Gson();
            FavList = gson.fromJson(FList, favList.getType());
            FavListIds = gson.fromJson(FListIds, favListIds.getType());
        }
        editor = this.getSharedPreferences("Therabeat", 0).edit();
        context = this;
        appBarLayout = findViewById(R.id.toolbarlayout);
        spinnerView = findViewById(R.id.cognitive_mode_selector);
        floatingLayout = findViewById(R.id.floating_layout);
        floatingActionButton = findViewById(R.id.fab1);
        AnxietyActionButton = findViewById(R.id.AnxietyActionButton);
        AttentionActionButton = findViewById(R.id.AttentionActionButton);
        MemoryActionButton = findViewById(R.id.MemoryActionButton);
        spinnerView.setIsFocusable(false);
        String mode = mSharedPreferences.getString("mode", "Memory");
        Log.d("mode", mode);
        DemoCollectionAdapter demoCollectionAdapter;
        ViewPager2 viewPager;
        switch (mode) {
            case "Memory":
                spinnerView.selectItemByIndex(0);
                appBarLayout.setBackgroundColor(Color.parseColor("#c8e6c9"));
                getWindow().setStatusBarColor(Color.parseColor("#c8e6c9"));
                break;
            case "Anxiety":
                spinnerView.selectItemByIndex(1);
                appBarLayout.setBackgroundColor(Color.parseColor("#bbdefb"));
                getWindow().setStatusBarColor(Color.parseColor("#bbdefb"));
                break;
            case "Attention":
                spinnerView.selectItemByIndex(2);
                appBarLayout.setBackgroundColor(Color.parseColor("#ffcdd2"));
                getWindow().setStatusBarColor(Color.parseColor("#ffcdd2"));
                break;
        }

        spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                ChangeMode(newIndex);

            }
        });
        floatingLayout.setOnMenuExpandedListener(new FloatingLayout.OnMenuExpandedListener() {
            @Override
            public void onMenuExpanded() {
                floatingActionButton.setFabIcon(getDrawable(R.drawable.ic_round_add_24));
                floatingActionButton.setText("");
            }

            @Override
            public void onMenuCollapsed() {
                floatingActionButton.setText("Switch Mode");
                Drawable d = getResources().getDrawable(R.drawable.ic_round_close_24);
                Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
                transparentDrawable.setBounds(new Rect(0, 0, d.getMinimumWidth(), d.getMinimumHeight()));
                floatingActionButton.setFabIcon(transparentDrawable);
            }
        });

        AttentionActionButton.setOnClickListener(view -> {
            ChangeMode(2);
            spinnerView.selectItemByIndex(2);
        });

        AnxietyActionButton.setOnClickListener(view -> {
            ChangeMode(1);
            spinnerView.selectItemByIndex(1);
        });

        MemoryActionButton.setOnClickListener(view -> {
            ChangeMode(0);
            spinnerView.selectItemByIndex(0);
        });
        getSongs();
        Toolbar toolbar = findViewById(R.id.actualtoolbar);
        toolbar.setTitle("Your Songs");

        setSupportActionBar(toolbar);

        demoCollectionAdapter = new DemoCollectionAdapter(this);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("FAVORITE");
                            break;
                        case 1:
                            tab.setText("SONGS");
                            break;
                        case 2:
                            tab.setText("ARTISTS");
                            break;
                        case 3:
                            tab.setText("ALBUMS");
                            break;
                        case 4:
                            tab.setText("PLAYLISTS");
                            break;
                    }
                }).attach();

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

    public void getSongs() {

        allAudioFiles = getAllAudioFromDevice(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        Handler x = new Handler();
        x.post(new Runnable() {
            @Override
            public void run() {

                SearchMenuItem = findViewById(R.id.action_search);
                AnalyticsMenuItem = findViewById(R.id.analytics);
                firstTimeOffline = mSharedPreferences.getBoolean("firstTimeOffline", true);
                Log.d("firstTimeOffline", String.valueOf(firstTimeOffline));
                if (firstTimeOffline) {
                    Log.d("inside", "oinside");
                    presentShowcaseSequence();
                }

            }
        });
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
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
                    adapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                    return true;
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
                        .replace(R.id.OfflineSettingsFragment, new Settings())
                        .commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<AudioModel> getAllAudioFromDevice(final Context context) {

        final List<AudioModel> tempAudioList = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0 ";
//                + "AND " + MediaStore.Audio.Media.DURATION + ">= 60000";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);
//        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null && cursor.getCount() > 0) {
            int i = 0;
            while (cursor.moveToNext()) {
                String path = cursor.getString(32);
                String title = cursor.getString(22);
                if (!title.contains(".aac")) {
                    String album = cursor.getString(34);
                    String artist = cursor.getString(9);

                    // Save to audioList
                    AudioModel audioModel = new AudioModel();
                    audioModel.setId(i);
                    audioModel.setaName(title);
                    audioModel.setaAlbum(album);
                    audioModel.setaArtist(artist);
                    audioModel.setaPath(path);
                    tempAudioList.add(audioModel);
                    i++;
                }
            }
        }
        return tempAudioList;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            Log.d("hello", "here");
            loadColorFromPreference();
        } else {
            if (!searchView.isIconified()) {
                searchView.onActionViewCollapsed();
            } else {
                super.onBackPressed();
            }
        }
    }

    //
    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("pushnotification")) {
            // If menuFragment is defined, then this activity was launched with a fragment selection
            AppUsageFragment appUsageFragment = new AppUsageFragment();
            appUsageFragment.show(getSupportFragmentManager(), "analyticsfrag");
        }
        loadColorFromPreference();
        ExoPlayer player = SingletonInstances.getInstance(this.getApplicationContext()).getExoPlayer();
        if (player.isPlaying()) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.offline_play_screen_frame_layout, (Fragment) BlankFragment.newInstance((AudioModel) player.getCurrentMediaItem().localConfiguration.tag)).setReorderingAllowed(true).commitAllowingStateLoss();
        }
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(spinnerView)
                        .setDismissText("GOT IT")
                        .setContentText("Switch between different cognitive modes and change Binaural Frequency.")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(SearchMenuItem, "Search for your favorite songs and add them to your playlist for quicker access.", "GOT IT");
        sequence.addSequenceItem(AnalyticsMenuItem, "View your analytics for specific cognitive modes.", "GOT IT");

        sequence.start();
        editor.putBoolean("firstTimeOffline", false);
        editor.apply();

    }

    private void loadColorFromPreference() {
        Log.d("ViewType", PreferenceManager.getDefaultSharedPreferences(this).getString("CognitiveModeViewType", getString(R.string.SpinnerOption)));
        changeTextColor(PreferenceManager.getDefaultSharedPreferences(this).getString("CognitiveModeViewType", getString(R.string.SpinnerOption)));
    }

    // Method to set Color of Text.
    private void changeTextColor(String pref_color_value) {
        Log.d("value", pref_color_value);
        if (pref_color_value.equals(getString(R.string.SpinnerOption))) {
            runOnUiThread(() -> {
                floatingLayout.setVisibility(View.GONE);
                spinnerView.setClickable(true);
            });
        } else {
            runOnUiThread(() -> {
                floatingLayout.setVisibility(View.VISIBLE);
                spinnerView.setClickable(false);
            });

        }
    }

    public static class DemoObjectFragment extends Fragment implements AudioListAdapter.ItemClickListener {
        public static final String ARG_OBJECT = "object";
        RecyclerView audioListView;
        AudioListAdapter audioListAdapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View inflatedView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            audioListView = inflatedView.findViewById(R.id.audioListView);
            Bundle args = getArguments();
            switch (args.getInt(ARG_OBJECT)) {
                case 2:
                    artistList.addAll(allAudioFiles);
                    Collections.sort(artistList, (AudioModel a1, AudioModel a2) -> a1.aAlbum.toUpperCase().compareTo(a2.aAlbum.toUpperCase()));
                    audioListAdapter = new AudioListAdapter(getActivity(), artistList, this, args.getInt(ARG_OBJECT), true);
                    break;
                case 3:
                    albumList.addAll(allAudioFiles);
                    Collections.sort(albumList, (AudioModel a1, AudioModel a2) -> a1.aAlbum.toUpperCase().compareTo(a2.aAlbum.toUpperCase()));
                    audioListAdapter = new AudioListAdapter(getActivity(), albumList, this, args.getInt(ARG_OBJECT), true);
                    break;
                case 1:
                    Collections.sort(allAudioFiles, (AudioModel a1, AudioModel a2) -> a1.aName.toUpperCase().compareTo(a2.aName.toUpperCase()));
                    audioListAdapter = new AudioListAdapter(getActivity(), allAudioFiles, this, args.getInt(ARG_OBJECT), true);
                    break;

            }
            if (args.getInt(ARG_OBJECT) == 0) {
                FavAdaptor = new AudioListAdapter(getActivity(), FavList, this, args.getInt(ARG_OBJECT), true);
                adapter = FavAdaptor;
                audioListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                audioListView.setAdapter(FavAdaptor);
                FavRecyclerView = audioListView;
            } else {
                adapter = audioListAdapter;
                audioListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                audioListView.setAdapter(audioListAdapter);
            }
            CheckBox mCheckBox = inflatedView.findViewById(R.id.favorite);
            return inflatedView;
        }

        @Override
        public void onItemClicked(AudioModel audio) {
            Log.d("name", audio.getaName());
            SingletonInstances.getInstance(getContext().getApplicationContext()).getExoPlayer().clearMediaItems();
            MediaItem NextSong = new MediaItem.Builder()
                    .setUri(Uri.fromFile(new File(audio.getaPath())))
                    .setTag(audio)
                    .build();

            SingletonInstances.getInstance(getContext().getApplicationContext()).getExoPlayer().addMediaItem(NextSong);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.offline_play_screen_frame_layout, (Fragment) BlankFragment.newInstance(audio)).setReorderingAllowed(true).commitAllowingStateLoss();
//        fragmentTransaction.commit();
        }
    }

    public class DemoCollectionAdapter extends FragmentStateAdapter {

        public DemoCollectionAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            if (position == 4) {
                Fragment fragment = new PlaylistView();
                return fragment;
            } else {
                Fragment fragment = new DemoObjectFragment();
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(DemoObjectFragment.ARG_OBJECT, position);
                fragment.setArguments(args);
                return fragment;
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
//
}