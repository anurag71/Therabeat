package com.anurag.therabeat;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

public class MusicPlayerActivity extends Fragment {

    private static final String TAG = "MainActivity";
    public static BeatsEngine wave;
    AudioModel audio;
    private ExoPlayer exoPlayer;
    private SeekBar seekPlayerProgress;
    private Handler handler;
    TextView TrackName;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;
    private Player.Listener eventListener = new Player.Listener() {
        @Override
        public void onEvents(Player player, Player.Events events) {
            Player.Listener.super.onEvents(player, events);
            Log.d("ExoPlayer", events.toString());
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
            Log.d("ExoPlayer Error", error.getMessage());
        }
    };
    private SharedPreferences msharedPreferences;
    TextView ArtistName;
    TextView TrackNameMin;
    TextView ArtistNameMin;
    private MaterialButton btnPlay;

    public MusicPlayerActivity() {
        // Required empty public constructor
    }

    public static MusicPlayerActivity newInstance(AudioModel audio) {
        MusicPlayerActivity fragment = new MusicPlayerActivity();
        Bundle bundle = new Bundle();
        bundle.putSerializable("audio", audio);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Log.d("here", "here");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.activity_music_player, container, false);
        TrackName = inflatedView.findViewById(R.id.audio_name_text_view);
        ArtistName = inflatedView.findViewById(R.id.artist_name_text_view);
        TrackNameMin = inflatedView.findViewById(R.id.audio_name_text_view_min);
        ArtistNameMin = inflatedView.findViewById(R.id.artist_name_text_view_min);
        txtCurrentTime = (TextView) inflatedView.findViewById(R.id.time_current);
        txtEndTime = (TextView) inflatedView.findViewById(R.id.player_end_time);
        btnPlay = inflatedView.findViewById(R.id.btnPlay);
        seekPlayerProgress = (SeekBar) inflatedView.findViewById(R.id.mediacontroller_progress);
        msharedPreferences = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance();
        wave = new Binaural(200, msharedPreferences.getFloat("beatFreq", 0.0F), 50);
        File file = new File(audio.getaPath());
        prepareExoPlayerFromFileUri(Uri.fromFile(file));
        TrackName.setText(audio.getaName());
        ArtistName.setText(audio.getaArtist());
        TrackNameMin.setText(audio.getaName());
        ArtistNameMin.setText(audio.getaArtist());
        TrackName.setSelected(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void prepareExoPlayerFromFileUri(Uri uri) {
        exoPlayer = new ExoPlayer.Builder(getActivity()).build();
        exoPlayer.addListener(eventListener);
        MediaItem firstItem = MediaItem.fromUri(uri);
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
//        MediaSource audioSource = new Exre(fileDataSource.getUri(),
//                factory, new DefaultExtractorsFactory(), null, null);
        exoPlayer.addMediaItem(firstItem);
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
        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration() / 1000);
        txtCurrentTime.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

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