package com.anurag.therabeat;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    //Class variables
    private String TAG = getClass().getSimpleName().toString();

    private Toolbar toolbar;

    public static BeatsEngine wave;
    final Fragment homeFragment = new HomeFragment();
    final Fragment searchFragment = new SearchFragment();
    final FragmentManager fm = getSupportFragmentManager();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment appUsageFragment = new AppUsageFragment();
    Fragment active = homeFragment;

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
                    fm.beginTransaction().hide(active).show(appUsageFragment).commit();
                    active = appUsageFragment;
                    return true;

                case R.id.settings:
                    fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    active = settingsFragment;
                    return true;
            }
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initializeView();
        msharedPreferences = this.getSharedPreferences("Therabeat", 0);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        wave = new Binaural(200, msharedPreferences.getFloat("beatFreq", 0.0F), 50);
        if (msharedPreferences.getBoolean("isPlaying", false)) {
            Log.d("check", "checking if playing");
        }

        fm.beginTransaction().add(R.id.main_container, searchFragment, "2").hide(searchFragment).commit();
        fm.beginTransaction().add(R.id.main_container, settingsFragment, "3").hide(settingsFragment).commit();
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