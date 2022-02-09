package com.anurag.therabeat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    //Class Variables
    private String TAG = getClass().getSimpleName().toString();

    private MaterialButton play_pause_image_view;
    private Button next_image_view;
    private Button prev_image_view;
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
        msharedPreferences = getActivity().getSharedPreferences("SPOTIFY", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_video, container, false);
        play_pause_image_view = inflatedView.findViewById(R.id.play_pause_image_view);
        next_image_view = inflatedView.findViewById(R.id.nextSongButton);
        prev_image_view = inflatedView.findViewById(R.id.prevSongButton);
        songNameTextViewMin = inflatedView.findViewById(R.id.audio_name_text_view_min);
        songNameTextViewMax = inflatedView.findViewById(R.id.audio_name_text_view);
        artistNameTextViewMin = inflatedView.findViewById(R.id.artist_name_text_view_min);
        getArtistNameTextViewMax = inflatedView.findViewById(R.id.artist_name_text_view);
        albumArtImageView = inflatedView.findViewById(R.id.album_art_image_view);
        beatVolumeSlider = inflatedView.findViewById(R.id.beatVolumeSlider);
        beatVolumeSlider.setValue(50);

        amplitudeFactor = beatVolumeSlider.getValue();
        beatFreq = beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);

        updateTextViews();

        beatVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    amplitudeFactor = value;
                    attemptStartwave(true);
                }
            }
        });

        play_pause_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_pause_image_view.isChecked()) {
                    play_pause_image_view.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_play_arrow_24_white));

                    mSpotifyAppRemote.getPlayerApi().pause();
                    UserMoodChoice.wave.stop();
                } else {
                    play_pause_image_view.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_pause_24_white));
                    if (beatFreq > 0.0) {
                        attemptStartwave(false);
                        mSpotifyAppRemote.getPlayerApi().resume();
                    }
                    play_pause_image_view.setChecked(false);
                }
            }
        });

        next_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipNext().setResultCallback(new CallResult.ResultCallback<Empty>() {
                    @Override
                    public void onResult(Empty data) {
                        updateTextViews();
                    }
                });
            }
        });

        prev_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious().setResultCallback(new CallResult.ResultCallback<Empty>() {
                    @Override
                    public void onResult(Empty data) {
                        updateTextViews();
                    }
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
        if (UserMoodChoice.wave != null) {
            UserMoodChoice.wave.release();
        }
        UserMoodChoice.wave = new Binaural(200, beatFreq, amplitudeFactor);
        if (!calledFromSlider) {
            if (!UserMoodChoice.wave.getIsPlaying()) {
                UserMoodChoice.wave.start();
                play_pause_image_view.setActivated(true);
                play_pause_image_view.setChecked(true);
            } else {
                play_pause_image_view.setActivated(false);
                play_pause_image_view.setChecked(false);
            }
        } else {
            UserMoodChoice.wave.start();
        }
    }
}