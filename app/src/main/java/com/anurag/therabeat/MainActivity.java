package com.anurag.therabeat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;

//Spotify
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.anurag.therabeat.connectors.UserService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener, NavigationBarView.OnItemSelectedListener{

	//Class variables
	private String TAG = getClass().getSimpleName().toString();

	private Toolbar toolbar;

	private TextView userView;
	private RecyclerView myView;

	private PlaylistService playlistService;
	public ArrayList<Playlist> playlistArrayList;

	public static BeatsEngine wave;
	private RequestQueue queue;

	//Spotify
	SpotifyConnection spotifyConnection;

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
		spotifyConnection = new SpotifyConnection(this);
		toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		initializeView();
		msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
		queue = Volley.newRequestQueue(this);
		AUTH_TOKEN = msharedPreferences.getString("token", "");
		waitForUserInfo();
		playlistService = new PlaylistService(MainActivity.this);
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

	public void setPlaylist(ArrayList<Playlist> playlist){
		playlistArrayList=playlist;
	}


	private void getPlaylists() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Fetching your Spotify Playlists");
		progressDialog.show();
		updatePlaylist();
		playlistArrayList = playlistService.getPlaylists(this.getApplicationContext(),this, myView, progressDialog);
	}

	public void updatePlaylist() {
		Log.d(TAG,"inside update playlsit");
			myView =  (RecyclerView)findViewById(R.id.recyclerview);
			myView.setHasFixedSize(true);
			LinearLayoutManager llm = new LinearLayoutManager(this);
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			myView.setLayoutManager(llm);
	}

	private void waitForUserInfo() {
		UserService userService = new UserService(queue, msharedPreferences);
		userService.get(() -> {
			User user = userService.getUser();
			editor = getSharedPreferences("SPOTIFY", 0).edit();
			editor.putString("userid", user.id);
			Log.d(TAG, "GOT USER INFORMATION");
			Log.d(TAG,user.display_name);
			// We use commit instead of apply because we need the information stored immediately
			editor.apply();
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG,AUTH_TOKEN);
	}

	private void connected(String playlistId) {
		spotifyConnection.mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:"+playlistId);
		isPlaying = true;
		// Subscribe to PlayerState
		spotifyConnection.mSpotifyAppRemote.getPlayerApi()
				.subscribeToPlayerState()
				.setEventCallback(playerState -> {
					final Track track = playerState.track;
					if (track != null) {
						Log.d(TAG, track.name + " by " + track.artist.name);
					}
				});
	}

	@Override
	protected void onStop() {
		super.onStop();
		SpotifyAppRemote.disconnect(spotifyConnection.mSpotifyAppRemote);
	}

	private void initializeView() {
//		frequencyCarrierInput = (EditText) findViewById(R.id.etCarrierFrequency);
//		frequencyBeatInput = (EditText) findViewById(R.id.etBeatFrequency);
//		displayCarrierFrequency = (TextView) findViewById(R.id.tvCarrierFrequency);

		//By default, and when someone changes anything, denote that data has been updated

		//Prepare noise track
		InputStream inputStream = getResources().openRawResource(R.raw.pinkwave);

	}

	private void togglePlay(float beatFreq, String playlistId) {
		connected(playlistId);
		attemptStartWave(beatFreq);
	}

	//if user goes too fast
	private void attemptStartWave(float beatFreq) {
		Log.d(TAG, String.valueOf(beatFreq));
		wave = new Binaural(200, beatFreq);
		wave.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onNoteClick(int position) {
		float beatFreq  = msharedPreferences.getFloat("beatFreq",0.0f);
		if(beatFreq==0.0){
			Toast toast = Toast.makeText(getApplicationContext(),"Please set a beat frequency in settings",Toast.LENGTH_SHORT);
			toast.show();
		}
		else{
			togglePlay(beatFreq,playlistArrayList.get(position).getId());
			this.getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, (Fragment)(new PlayerFragment())).setReorderingAllowed(true).commitAllowingStateLoss();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		return false;
	}
}