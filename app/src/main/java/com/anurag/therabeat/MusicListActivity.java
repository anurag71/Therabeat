package com.anurag.therabeat;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sevenshifts.sideheaderdecorator.SideHeaderDecorator;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

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
    public static List<AudioModel> FavList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);


        //Checks permissions

        //Finds all the mp3 files in device and makes a list
//        getSongs();
        mSharedPreferences = this.getSharedPreferences("Therabeat", 0);
        String FList = mSharedPreferences.getString("FavList", "");
        if (FList.equals("")) {
            FavList = new ArrayList<>();
        } else {
            TypeToken<ArrayList<AudioModel>> token = new TypeToken<ArrayList<AudioModel>>() {
            };
            Gson gson = new Gson();
            FavList = gson.fromJson(FList, token.getType());
        }
        editor = this.getSharedPreferences("Therabeat", 0).edit();
        context = this;
        appBarLayout = findViewById(R.id.toolbarlayout);
        spinnerView = findViewById(R.id.cognitive_mode_selector);
        spinnerView.setIsFocusable(false);
        String mode = mSharedPreferences.getString("mode", "Memory");
        Log.d("mode", mode);
        DemoCollectionAdapter demoCollectionAdapter;
        ViewPager2 viewPager;
        switch (mode) {
            case "Memory":
                Log.d("insdie", "memory");
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
                    }
                }).attach();

    }

    public void getSongs(){

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
//                    presentShowcaseSequence();
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
            while (cursor.moveToNext()) {
                String path = cursor.getString(32);
                String title = cursor.getString(22);
                if (!title.contains(".aac")) {
                    String album = cursor.getString(34);
                    String artist = cursor.getString(9);

                    // Save to audioList
                    AudioModel audioModel = new AudioModel();
                    audioModel.setaName(title);
                    audioModel.setaAlbum(album);
                    audioModel.setaArtist(artist);
                    audioModel.setaPath(path);
                    tempAudioList.add(audioModel);
                }
            }
        }
        return tempAudioList;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        else {
            if (!searchView.isIconified()) {
                searchView.onActionViewCollapsed();
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
            return inflatedView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            Bundle args = getArguments();
            switch (args.getInt(ARG_OBJECT)) {
                case 2:
                    Collections.sort(allAudioFiles, (AudioModel a1, AudioModel a2) -> a1.aArtist.toUpperCase().compareTo(a2.aArtist.toUpperCase()));
                    break;
                case 3:
                    Collections.sort(allAudioFiles, (AudioModel a1, AudioModel a2) -> a1.aAlbum.toUpperCase().compareTo(a2.aAlbum.toUpperCase()));
                    break;
                default:
                    Collections.sort(allAudioFiles, (AudioModel a1, AudioModel a2) -> a1.aName.toUpperCase().compareTo(a2.aName.toUpperCase()));
                    break;

            }
            if (args.getInt(ARG_OBJECT) == 0) {
                FavAdaptor = new AudioListAdapter(getActivity(), FavList, this, args.getInt(ARG_OBJECT));
                adapter = FavAdaptor;
                audioListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                audioListView.setAdapter(FavAdaptor);
                FavRecyclerView = audioListView;
            } else {
                audioListAdapter = new AudioListAdapter(getActivity(), allAudioFiles, this, args.getInt(ARG_OBJECT));
                adapter = audioListAdapter;
                audioListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                audioListView.setAdapter(audioListAdapter);
            }
            CheckBox mCheckBox = view.findViewById(R.id.favorite);
            SideHeaderDecorator.HeaderProvider headerProvider = new SideHeaderDecorator.HeaderProvider() {
                @Override
                public Object getHeader(int i) {
                    Object header;
                    switch (args.getInt(ARG_OBJECT)) {
                        case 2:
                            header = allAudioFiles.get(i).getaArtist().toUpperCase().charAt(0);
                            break;
                        case 3:
                            header = allAudioFiles.get(i).getaAlbum().toUpperCase().charAt(0);
                            break;

                        default:
                            header = allAudioFiles.get(i).getaName().toUpperCase().charAt(0);
                    }
                    return header;
                }
            };

            SideHeaderDecorator sideHeaderDecorator = new SideHeaderDecorator(headerProvider) {
                @NonNull
                @Override
                public View getHeaderView(Object o, @NonNull RecyclerView recyclerView) {
                    TextView textView = (TextView) LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.header_view, recyclerView, false);
                    textView.setText(o.toString());

                    return textView;
                }
            };
            audioListView.addItemDecoration(sideHeaderDecorator);
        }

        @Override
        public void onItemClicked(AudioModel audio) {
            Log.d("name", audio.getaName());
//        MusicPlayerActivity fragment = MusicPlayerActivity.newInstance(audio);
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.replace(R.id.offline_play_screen_frame_layout, (Fragment) MusicPlayerActivity.newInstance(audio));
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
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(DemoObjectFragment.ARG_OBJECT, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
//
}