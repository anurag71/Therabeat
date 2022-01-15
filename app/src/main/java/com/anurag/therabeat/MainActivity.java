package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;

//Spotify
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.UserService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener, NavigationBarView.OnItemSelectedListener{

	//Class variables
	private Toolbar toolbar;

	private TextView userView;
	private RecyclerView myView;

	private PlaylistService playlistService;
	private ArrayList<Playlist> playlistArrayList;

	private MaterialButton startStop;
	private boolean isDataChanged = true, isCarrierValid = true, isBeatValid = true;
	private BeatsEngine wave;
	private RequestQueue queue;

	//Spotify
	private static final String CLIENT_ID = "a98fdf7072d24d9dbf8999a6d74212b0";
	private static final String REDIRECT_URI = "http://com.anurag.therabeat/callback";
	private SpotifyAppRemote mSpotifyAppRemote;

	private String AUTH_TOKEN="";

	int position;

	//Refresh waveform if user changes frequencies

	private boolean isInitial = true;
	private boolean isPlaying = false;

	private SharedPreferences.Editor editor;
	private SharedPreferences msharedPreferences;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		initializeView();
		msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
		queue = Volley.newRequestQueue(this);
		AUTH_TOKEN = msharedPreferences.getString("token", "");
		if(!AUTH_TOKEN.equals("")){
			Toast toast = Toast.makeText(getApplicationContext(),"Successfully Connected to Spotify",Toast.LENGTH_SHORT);
			toast.show();
		}

		waitForUserInfo();

		playlistService = new PlaylistService(getApplicationContext());
		userView = (TextView) findViewById(R.id.user);

		userView.setText("Please select a playlist from below");

		getPlaylists();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.custom_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.nav_settings){
			Intent intent = new Intent(MainActivity.this,

					SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}



	private void getPlaylists() {
		playlistService.getAllPlaylists(() -> {
			playlistArrayList = playlistService.getPlaylists();
			updatePlaylist();
		});
	}

	private void updatePlaylist() {
		if (playlistArrayList.size() > 0) {
			RecyclerViewAdapter adapter = new RecyclerViewAdapter(playlistArrayList,this);
			myView =  (RecyclerView)findViewById(R.id.recyclerview);
			myView.setHasFixedSize(true);
			myView.setAdapter(adapter);
			LinearLayoutManager llm = new LinearLayoutManager(this);
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			myView.setLayoutManager(llm);
		}
	}

	private void waitForUserInfo() {
		UserService userService = new UserService(queue, msharedPreferences);
		userService.get(() -> {
			User user = userService.getUser();
			editor = getSharedPreferences("SPOTIFY", 0).edit();
			editor.putString("userid", user.id);
			Log.d("STARTING", "GOT USER INFORMATION");
			Log.d("STARTING",user.display_name);
			// We use commit instead of apply because we need the information stored immediately
			editor.apply();
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		ConnectionParams connectionParams =
				new ConnectionParams.Builder(CLIENT_ID)
						.setRedirectUri(REDIRECT_URI)
						.showAuthView(true)
						.build();

		SpotifyAppRemote.connect(this, connectionParams,
				new Connector.ConnectionListener() {

					public void onConnected(SpotifyAppRemote spotifyAppRemote) {
						mSpotifyAppRemote = spotifyAppRemote;
						Log.d("MainActivity", "Connected! Yay!");

					}

					public void onFailure(Throwable throwable) {
						Log.e("MyActivity", throwable.getMessage(), throwable);

						// Something went wrong when attempting to connect! Handle errors here
					}
				});
		Log.d("MainActivity",AUTH_TOKEN);
	}

	private void connected(String playlistId) {
		mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:"+playlistId);
		isPlaying = true;
		// Subscribe to PlayerState
		mSpotifyAppRemote.getPlayerApi()
				.subscribeToPlayerState()
				.setEventCallback(playerState -> {
					final Track track = playerState.track;
					if (track != null) {
						Log.d("MainActivity", track.name + " by " + track.artist.name);
					}
				});
	}

	@Override
	protected void onStop() {
		super.onStop();
		SpotifyAppRemote.disconnect(mSpotifyAppRemote);
	}

	private void initializeView() {
//		frequencyCarrierInput = (EditText) findViewById(R.id.etCarrierFrequency);
//		frequencyBeatInput = (EditText) findViewById(R.id.etBeatFrequency);
		startStop = (MaterialButton) findViewById(R.id.btnPlay);
		startStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clickPlay();
			}
		});
//		displayCarrierFrequency = (TextView) findViewById(R.id.tvCarrierFrequency);

		//By default, and when someone changes anything, denote that data has been updated

		//Prepare noise track
		InputStream inputStream = getResources().openRawResource(R.raw.pinkwave);

	}

	//Play button clicked
	public void clickPlay() {
		float beatFreq  = msharedPreferences.getFloat("beatFreq",0.0f);
		String playlistId = msharedPreferences.getString("playlistId","No Id set");
		Log.d("MainActivity",playlistId);
		if(beatFreq==0.0){
			Toast toast = Toast.makeText(getApplicationContext(),"Please set a beat frequency in settings",Toast.LENGTH_SHORT);
			toast.show();
		}
		else if(playlistId==""){
			Toast toast = Toast.makeText(getApplicationContext(),"No playlist selected",Toast.LENGTH_SHORT);
			toast.show();
		}
		else{
			togglePlay(beatFreq,playlistId);
		}
	}

	private void togglePlay(float beatFreq, String playlistId) {
			if (startStop.isChecked()) {
				startStop.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_pause_circle_filled_24));
				if(isInitial && !isPlaying){
					isInitial=false;
					connected(playlistId);
				}
				else{
					isPlaying=true;
					mSpotifyAppRemote.getPlayerApi().resume();
				}
				//wave.start();
				attemptStartWave(beatFreq);
			} else {
				startStop.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_round_play_circle_filled_24));
				isPlaying = false;
				mSpotifyAppRemote.getPlayerApi().pause();
				wave.stop();
			}
		}

	//if user goes too fast
	private void attemptStartWave(float beatFreq) {
		if (wave != null) {
			wave.release();
		}
		Log.d("MainActivity", String.valueOf(beatFreq));
		wave = new Binaural(200, beatFreq);
		if (!wave.getIsPlaying()) {
			wave.start();
			startStop.setActivated(true);
			startStop.setChecked(true);
		} else {
			startStop.setActivated(false);
			startStop.setChecked(false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onNoteClick(int position) {
		editor = getSharedPreferences("SPOTIFY", 0).edit();
		editor.putString("playlistId", playlistArrayList.get(position).getId());
		editor.apply();
		this.getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment)(new PlayScreenFragment())).commitAllowingStateLoss();
		Toast toast = Toast.makeText(getApplicationContext(),"This playlist is selected to be played",Toast.LENGTH_SHORT);
		toast.show();
		isInitial=true;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		return false;
	}
}