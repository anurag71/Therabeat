package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.anurag.therabeat.connectors.UserService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	//Class variables
	private String TAG = getClass().getSimpleName().toString();

	private Toolbar toolbar;

	final Fragment fragment1 = new HomeFragment();
	final Fragment fragment2 = new SearchFragment();
	final FragmentManager fm = getSupportFragmentManager();
	Fragment active = fragment1;

	//Refresh waveform if user changes frequencies

	private boolean isInitial = true;
	private boolean isPlaying = false;

	private SharedPreferences.Editor editor;
	private SharedPreferences msharedPreferences;
	private boolean exception = false;
	private String errorMsg="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		initializeView();
		msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
//		waitForUserInfo();
//		fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
		fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();
	}

	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.home:
					fm.beginTransaction().hide(active).show(fragment1).commit();
					active = fragment1;
					return true;

				case R.id.search:
					fm.beginTransaction().hide(active).show(fragment2).commit();
					active = fragment2;
					return true;
			}
			return false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.custom_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.nav_settings) {
			Intent intent = new Intent(MainActivity.this,

					SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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