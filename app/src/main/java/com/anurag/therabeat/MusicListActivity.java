package com.anurag.therabeat;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    private static final String SHOWCASE_ID = "FirstTimeTutorial";
    RecyclerView audioListView;
    AudioListAdapter audioListAdapter;
    Context context;
    List<AudioModel> allAudioFiles;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        //Finds all the mp3 files in device and makes a list
        getSongs();

        mSharedPreferences = this.getSharedPreferences("Therabeat", 0);
        editor = this.getSharedPreferences("Therabeat", 0).edit();
        context = this;
        appBarLayout = findViewById(R.id.toolbarlayout);
        spinnerView = findViewById(R.id.cognitive_mode_selector);
        spinnerView.setIsFocusable(false);
        switch (mSharedPreferences.getString("mode", "Memory")) {
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

        Toolbar toolbar = findViewById(R.id.actualtoolbar);
        toolbar.setTitle("Your Songs");

        setSupportActionBar(toolbar);


    }

    public void getSongs(){
        context = MusicListActivity.this;
        audioListView = findViewById(R.id.audioListView);

        allAudioFiles = getAllAudioFromDevice(context);

        audioListAdapter = new AudioListAdapter(context,allAudioFiles);
        audioListView.setLayoutManager(new LinearLayoutManager(context));
        audioListView.setAdapter(audioListAdapter);
        SideHeaderDecorator.HeaderProvider headerProvider = new SideHeaderDecorator.HeaderProvider() {
            @Override
            public Object getHeader(int i) {
                return allAudioFiles.get(i).getaName().toUpperCase().charAt(0);
            }
        };

        SideHeaderDecorator sideHeaderDecorator = new SideHeaderDecorator(headerProvider) {
            @NonNull
            @Override
            public View getHeaderView(Object o, @NonNull RecyclerView recyclerView) {
                TextView textView = (TextView) LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.header_view,recyclerView,false);
                textView.setText(o.toString());

                return textView;
            }
        };
        audioListView.addItemDecoration(sideHeaderDecorator);
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
                if(!title.contains(".aac")){
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
        Collections.sort(tempAudioList, (AudioModel a1, AudioModel a2) -> a1.aName.toUpperCase().compareTo(a2.aName.toUpperCase()));
        return tempAudioList;
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSongs();
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                firstTimeOffline = mSharedPreferences.getBoolean("firstTimeOffline", true);
                if (firstTimeOffline) {
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
                    audioListAdapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    audioListAdapter.filter(newText);
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
            case R.id.aboutusitem:
                Intent intent;
                intent = new Intent(MusicListActivity.this,

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
                appUsageFragment.show(getSupportFragmentManager(), "analyticsfrag");

            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
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
}