package com.anurag.therabeat;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    private static final String TAG = "MainActivity";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static BeatsEngine wave;
    AudioModel audio;
    TextView TrackName;
    TextView ArtistName;
    TextView TrackNameMin;
    TextView ArtistNameMin;
    private Button NextSongButton;
    private Button PrevSongButton;
    private Button SetShuffleButton;
    private Button SetRepeatButton;
    private ExoPlayer exoPlayer;
    private SeekBar seekPlayerProgress;
    private Handler handler;
    private MaterialButton btnPlay;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;
    private Player.Listener eventListener = new Player.Listener() {
        @Override
        public void onEvents(Player player, Player.Events events) {
            Player.Listener.super.onEvents(player, events);
            Log.d("ExoPlayer", events.toString());
        }

        @Override
        public void onMediaItemTransition(
                @Nullable MediaItem mediaItem, @Player.MediaItemTransitionReason int reason) {
            if (reason != Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                updateUiForPlayingMediaItem(mediaItem);
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
            Log.d("ExoPlayer Error", error.getMessage());
        }

        @Override
        public void onTracksInfoChanged(TracksInfo tracksInfo) {
            // Update UI using current TracksInf
        }
    };

    private SharedPreferences msharedPreferences;
    private Slider beatVolumeSlider;
    private Button ClosePlayerButton;
    private SharedPreferences mSharedPreferences;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(AudioModel param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            audio = (AudioModel) getArguments().getSerializable(ARG_PARAM1);
            Log.d("name", audio.getaName());
        }
        mSharedPreferences = getActivity().getSharedPreferences("Therabeat", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_blank, container, false);
//        CardView PlayerBackground = inflatedView.findViewById(R.id.player_background_view);
//        String mode = mSharedPreferences.getString("mode", "Memory");
//        switch (mode) {
//            case "Memory":
//                PlayerBackground.setBackgroundColor(Color.parseColor("#c8e6c9"));
//                break;
//            case "Anxiety":
//                PlayerBackground.setBackgroundColor(Color.parseColor("#bbdefb"));
//                break;
//            case "Attention":
//                PlayerBackground.setBackgroundColor(Color.parseColor("#ffcdd2"));
//                break;
//        }
        exoPlayer = SingletonInstances.getInstance(getActivity().getApplicationContext()).getExoPlayer();
        exoPlayer.stop();
        TrackName = inflatedView.findViewById(R.id.audio_name_text_view);
        ArtistName = inflatedView.findViewById(R.id.artist_name_text_view);
        TrackNameMin = inflatedView.findViewById(R.id.audio_name_text_view_min);
        ArtistNameMin = inflatedView.findViewById(R.id.artist_name_text_view_min);
        txtCurrentTime = (TextView) inflatedView.findViewById(R.id.time_current);
        txtEndTime = (TextView) inflatedView.findViewById(R.id.player_end_time);
        btnPlay = inflatedView.findViewById(R.id.btnPlay);
        seekPlayerProgress = (SeekBar) inflatedView.findViewById(R.id.mediacontroller_progress);
        beatVolumeSlider = inflatedView.findViewById(R.id.beatVolumeSlider);
        ClosePlayerButton = inflatedView.findViewById(R.id.closePlayerButton);
        NextSongButton = inflatedView.findViewById(R.id.nextSongButton);
        PrevSongButton = inflatedView.findViewById(R.id.prevSongButton);
        SetShuffleButton = inflatedView.findViewById(R.id.SetShuffle);
        SetRepeatButton = inflatedView.findViewById(R.id.SetRepeat);
        beatVolumeSlider.setValue(50);
        msharedPreferences = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance();
        wave = new Binaural(200, msharedPreferences.getFloat("beatFreq", 0.0F), 50);
        return inflatedView;
    }

    private void updateUiForPlayingMediaItem(MediaItem mediaItem) {

        AudioModel metadata = (AudioModel) mediaItem.localConfiguration.tag;

        TrackName.setText(metadata.getaName());
        ArtistName.setText(metadata.getaArtist());
        TrackNameMin.setText(metadata.getaName());
        ArtistNameMin.setText(metadata.getaArtist());

        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);
        txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(audio.getaPath());
        prepareExoPlayerFromFileUri(Uri.fromFile(file));
        beatVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    wave.setVolume(value);
                }
            }
        });

        NextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("mediaPlayer", String.valueOf(exoPlayer.getMediaItemCount()));
                if (exoPlayer.hasNextMediaItem()) {
                    exoPlayer.seekToNextMediaItem();
                }
            }
        });

        PrevSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer.hasPreviousMediaItem()) {
                    exoPlayer.seekToPreviousMediaItem();
                }
            }
        });

        SetShuffleButton.setOnClickListener(view1 -> {
            SetShuffleButton.setActivated(!SetShuffleButton.isActivated());
            if (SetShuffleButton.isActivated()) {
                exoPlayer.setShuffleModeEnabled(true);
            } else {
                exoPlayer.setShuffleModeEnabled(false);
            }
        });

        SetRepeatButton.setOnClickListener(view1 -> {
            SetRepeatButton.setActivated(!SetRepeatButton.isActivated());
            if (SetRepeatButton.isActivated()) {
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            } else {
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
        });

        ClosePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.clearMediaItems();
                    exoPlayer.stop();
                    wave.stop();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(BlankFragment.this).commit();
            }
        });
    }

    private void prepareExoPlayerFromFileUri(Uri uri) {
        exoPlayer.addListener(eventListener);

        MediaItem firstItem = new MediaItem.Builder()
                .setUri(uri)
                .setTag(audio)
                .build();

        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
//        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
//                factory, new DefaultExtractorsFactory(), null, null);
//        MediaBrowser.MediaItem item = MediaBrowser.MediaItem.fromUri
        updateUiForPlayingMediaItem(firstItem);
        exoPlayer.prepare();
        initMediaControls();
    }

    private void initMediaControls() {
        initPlayButton();
        initSeekBar();
        initTxtTime();
    }

    private void initPlayButton() {
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
    }

    /**
     * Starts or stops playback. Also takes care of the Play/Pause button toggling
     *
     * @param play True if playback should be started
     */
    private void setPlayPause(boolean play) {
        isPlaying = play;
        exoPlayer.setPlayWhenReady(play);
        if (!isPlaying) {
            btnPlay.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_round_play_arrow_24_white));
            wave.stop();
        } else {
            setProgress();
            wave.start();
            btnPlay.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_round_pause_24_white));
        }
    }

    private void initTxtTime() {

    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {
                    seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initSeekBar() {
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);

    }
}