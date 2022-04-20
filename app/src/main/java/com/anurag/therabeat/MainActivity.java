package com.anurag.therabeat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anurag.therabeat.Database.AnxietyUsage;
import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AppExecutors;
import com.anurag.therabeat.Database.AttentionUsage;
import com.anurag.therabeat.Database.MemoryUsage;
import com.anurag.therabeat.Database.TotalUsage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Class variables
    private String TAG = getClass().getSimpleName().toString();

    private Toolbar toolbar;

    public static BeatsEngine wave;
    final Fragment homeFragment = new HomeFragment();
    final Fragment searchFragment = new SearchFragment();
    final FragmentManager fm = getSupportFragmentManager();
    final Fragment settingsFragment = new Settings();
    final Fragment appUsageFragment = new AppUsageFragment();
    Fragment active = homeFragment;
    AppDatabase db;

    //Refresh waveform if user changes frequencies

    private boolean isInitial = true;
    private boolean isPlaying = false;

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private boolean exception = false;
	private String errorMsg = "";
	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    fm.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    return true;

                case R.id.search:
                    fm.beginTransaction().hide(active).show(searchFragment).commit();
                    active = searchFragment;
                    return true;

                case R.id.appUsage:
                    fm.beginTransaction().hide(active).detach(appUsageFragment).attach(appUsageFragment).show(appUsageFragment).commit();
                    active = appUsageFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(MainActivity.this);
        dialog.setTitle("Exit Therabeat")
                .setMessage("Would would you like to do?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("View Your Analytics", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fm.beginTransaction().hide(active).show(appUsageFragment).commit();
                        active = appUsageFragment;
                        BottomNavigationView bottomNavigationView;
                        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
                        bottomNavigationView.setSelectedItemId(R.id.appUsage);
                    }
                })
                .setNegativeButton("Switch Cognitive Mode", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNeutralButton("Exit Therabeat", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this.getApplicationContext());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializeView();
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


        msharedPreferences = this.getSharedPreferences("Therabeat", 0);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        wave = new Binaural(200, msharedPreferences.getFloat("beatFreq", 0.0F), 50);
        if (msharedPreferences.getBoolean("isPlaying", false)) {
            Log.d("check", "checking if playing");
        }

        fm.beginTransaction().add(R.id.main_container, searchFragment, "2").hide(searchFragment).commit();
        fm.beginTransaction().add(R.id.main_container, appUsageFragment, "3").hide(appUsageFragment).commit();
        fm.beginTransaction().add(R.id.main_container, homeFragment, "1").commit();
    }

//	public void setPlaylist(ArrayList<Song> playlist) {
//		playlistArrayList = playlist;
//	}
//
//
//	private void getPlaylists(String searchQuery) {
//		updatePlaylist();
//		RecyclerViewAdapter adapter = new RecyclerViewAdapter(playlistArrayList, this);
//		myView.setAdapter(adapter);
//		playlistArrayList = playlistService.getPlaylists(this.getApplicationContext(), searchQuery, this, adapter);
//
//	}
//
//	public void updatePlaylist() {
//		Log.d(TAG, "inside update playlsit");
//		myView = (RecyclerView) findViewById(R.id.recyclerview);
//			myView.setHasFixedSize(true);
//			LinearLayoutManager llm = new LinearLayoutManager(this);
//			llm.setOrientation(LinearLayoutManager.VERTICAL);
//			myView.setLayoutManager(llm);
//	}
//
//	private void waitForUserInfo() {
//		UserService userService = new UserService(queue, msharedPreferences);
//		userService.get(() -> {
//			User user = userService.getUser();
//			editor = getSharedPreferences("SPOTIFY", 0).edit();
//			editor.putString("userid", user.id);
//			Log.d(TAG, "GOT USER INFORMATION");
//			Log.d(TAG,user.display_name);
//			// We use commit instead of apply because we need the information stored immediately
//			editor.apply();
//		});
//	}

	@Override
	protected void onStart() {
		super.onStart();
	}

//	private void connected(String uri) {
//		spotifyConnection.mSpotifyAppRemote.getPlayerApi().play(uri);
//		spotifyConnection.mSpotifyAppRemote.getPlayerApi()
//				.subscribeToPlayerState()
//				.setEventCallback(playerState -> {
//					final Track track = playerState.track;
//					if (track != null) {
//						Log.d(TAG, track.name + " by " + track.artist.name);
//					}
//				});
//		isPlaying = true;
//		// Subscribe to PlayerState
//
//	}

	@Override
	protected void onStop() {
        super.onStop();
//		SpotifyAppRemote.disconnect(spotifyConnection.mSpotifyAppRemote);
        if (isChangingConfigurations() && PlayerFragment.newInstance().isPlaying) {
            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing");
        }
//		else {
//			SpotifyConnection spotifyConnection = new SpotifyConnection(this);
//			spotifyConnection.getPlayerInstance(this);
//			spotifyConnection.mSpotifyAppRemote.getPlayerApi().pause();
//			MainActivity.wave.release();
//			SpotifyAppRemote.disconnect(spotifyConnection.mSpotifyAppRemote);
//		}

    }

	private void initializeView() {
//		frequencyCarrierInput = (EditText) findViewById(R.id.etCarrierFrequency);
//		frequencyBeatInput = (EditText) findViewById(R.id.etBeatFrequency);
//		displayCarrierFrequency = (TextView) findViewById(R.id.tvCarrierFrequency);

		//By default, and when someone changes anything, denote that data has been updated

		//Prepare noise track
		InputStream inputStream = getResources().openRawResource(R.raw.pinkwave);

	}

//	private void togglePlay(float beatFreq, String playlistId) {
//		connected(playlistId);
//		attemptStartWave(beatFreq);
//	}
//
//	//if user goes too fast
//	private void attemptStartWave(float beatFreq) {
//		Log.d(TAG, String.valueOf(beatFreq));
//		wave = new Binaural(200, beatFreq, 50);
//		wave.start();
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

//	public void onNoteClick(int position) {
//		float beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);
//		Log.d(TAG, playlistArrayList.get(position).getName());
//		togglePlay(beatFreq, playlistArrayList.get(position).getUri());
//		this.getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment) (new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
//	}
//
//	private void displayError(){
//		TextView errorTextView = findViewById(R.id.SplashActivityErrorTextView);
//		errorTextView.setText(errorMsg);
//		errorTextView.setVisibility(View.VISIBLE);
//	}

//	@Override
//	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//		return false;
//	}
}