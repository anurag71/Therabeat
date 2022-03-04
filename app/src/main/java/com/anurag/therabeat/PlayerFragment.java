package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AppExecutors;
import com.anurag.therabeat.Database.Person;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.Track;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    //Class Variables
    private String TAG = getClass().getSimpleName().toString();

    private MaterialButton play_pause_image_view;
    private Button NextSongButton;
    private Button PrevSongButton;
    private MaterialButton SetShuffleButton;
    private Button SetRepeatButton;
    private Button SpotifyLinkingButton;
    private TextView songNameTextViewMin;
    private TextView songNameTextViewMax;
    private TextView artistNameTextViewMin;
    private TextView getArtistNameTextViewMax;
    private ImageView albumArtImageView;
    private Slider beatVolumeSlider;


    // TODO: Rename and change types of parameters
    SpotifyConnection spotifyConnection;
    private SharedPreferences msharedPreferences;
    private SpotifyAppRemote mSpotifyAppRemote;

    private float amplitudeFactor;
    private float beatFreq;
    public boolean isPlaying = true;

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Calendar c = Calendar.getInstance();
    String date = sdf.format(c.getTime());
    AppDatabase db;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyConnection = new SpotifyConnection(getContext());
        mSpotifyAppRemote = spotifyConnection.mSpotifyAppRemote;
//        appUsageDao = SingletonInstances.getInstance(getActivity().getApplicationContext()).getDbInstance().appUsageDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = AppDatabase.getInstance(getActivity().getApplicationContext());
        msharedPreferences = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance();
        View inflatedView = inflater.inflate(R.layout.fragment_player, container, false);
        play_pause_image_view = inflatedView.findViewById(R.id.play_pause_image_view);
        NextSongButton = inflatedView.findViewById(R.id.nextSongButton);
        PrevSongButton = inflatedView.findViewById(R.id.prevSongButton);
        SetShuffleButton = inflatedView.findViewById(R.id.SetShuffle);
        SetRepeatButton = inflatedView.findViewById(R.id.SetRepeat);
        SpotifyLinkingButton = inflatedView.findViewById(R.id.SpotifyLink);
        songNameTextViewMin = inflatedView.findViewById(R.id.audio_name_text_view_min);
        songNameTextViewMax = inflatedView.findViewById(R.id.audio_name_text_view);
        artistNameTextViewMin = inflatedView.findViewById(R.id.artist_name_text_view_min);
        getArtistNameTextViewMax = inflatedView.findViewById(R.id.artist_name_text_view);
        albumArtImageView = inflatedView.findViewById(R.id.album_art_image_view);
        beatVolumeSlider = inflatedView.findViewById(R.id.beatVolumeSlider);
        beatVolumeSlider.setValue(50);

        amplitudeFactor = beatVolumeSlider.getValue();
        beatFreq = beatFreq = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance().getFloat("beatFreq", 0.0f);

        updateTextViews();

        beatVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    MainActivity.wave.setVolume(value);
                }
            }
        });

        play_pause_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_pause_image_view.isChecked()) {
                    play_pause_image_view.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_play_arrow_24_white));

                    mSpotifyAppRemote.getPlayerApi().pause();
                    MainActivity.wave.stop();
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Person p = db.personDao().loadPersonById(date);
                            if (p == null) {
                                db.personDao().insertPerson(new Person(date, 0));
                            }
                            p = db.personDao().loadPersonById(date);
                            Long usage = Long.valueOf(p.getTimeUsed());
                            usage = usage + ((System.currentTimeMillis() / 1000) - msharedPreferences.getLong("startTime", (long) 0.0));
                            db.personDao().insertPerson(new Person(date, usage.intValue()));
                        }
                    });
//                    Long usage = appUsageDao.fetchTimeForDate(date).getTimeUsed();
//                    appUsageDao.insert(new AppUsageHistory(date,usage + (end - start)));
                    isPlaying = false;
                } else {
                    play_pause_image_view.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_pause_24_white));
                    if (beatFreq > 0.0) {
                        attemptStartwave(false);
                        mSpotifyAppRemote.getPlayerApi().resume();
                        isPlaying = true;
                    }
                    play_pause_image_view.setChecked(false);
                }
            }
        });

        NextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSpotifyAppRemote.getPlayerApi().skipNext();
                updateTextViews();
//                Log.d("check Button","button pressed");
            }
        });

        PrevSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious();
            }
        });
//        buttonEffect(SetShuffleButton);
        SetShuffleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (SetShuffleButton.isChecked()) {
                    mSpotifyAppRemote.getPlayerApi().setShuffle(true);
                    Toast toast = Toast.makeText(getActivity(), "Shuffle Enabled", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Log.d("shuffle", "unchecked");
                    mSpotifyAppRemote.getPlayerApi().setShuffle(false);
                    Toast toast = Toast.makeText(getActivity(), "Shuffle Disabled", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        });

        SetRepeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (SetShuffleButton.isChecked()) {
                    mSpotifyAppRemote.getPlayerApi().setRepeat(Repeat.ALL);
                    Toast toast = Toast.makeText(getActivity(), "Repeating All Songs", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Log.d("repeat", "unchecked");
                    mSpotifyAppRemote.getPlayerApi().setRepeat(Repeat.OFF);
                    Toast toast = Toast.makeText(getActivity(), "Repeat Disabled", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        });

        SpotifyLinkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.putExtra(Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://" + getActivity().getPackageName()));
                final String[] track1 = {""};
                mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(data -> {
                    intent.setData(Uri.parse(data.track.uri));
                    startActivity(intent);
                });

            }
        });


        return inflatedView;
    }

    private void updateTextViews() {
        String songName;
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            albumArtImageView.setImageBitmap(bitmap);
                                        });
                        Log.d(TAG, track.uri);
                        songNameTextViewMax.setText(track.name);
                        songNameTextViewMin.setText(track.name);
                        artistNameTextViewMin.setText(track.artist.name);
                        getArtistNameTextViewMax.setText(track.artist.name);

                    }
                });

    }

    private void attemptStartwave(boolean calledFromSlider) {
        if (!calledFromSlider) {
            if (!MainActivity.wave.getIsPlaying()) {
                MainActivity.wave.start();
                msharedPreferences.edit().putLong("startTime", System.currentTimeMillis() / 1000).apply();
                play_pause_image_view.setActivated(true);
                play_pause_image_view.setChecked(true);
            } else {
                play_pause_image_view.setActivated(false);
                play_pause_image_view.setChecked(false);
            }
        } else {
            MainActivity.wave = new Binaural(200, beatFreq, amplitudeFactor);
            if (MainActivity.wave.getIsPlaying()) {
                MainActivity.wave.start();
            }
        }
    }
}