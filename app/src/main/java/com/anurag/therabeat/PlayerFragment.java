package com.anurag.therabeat;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.therabeat.connectors.SpotifyConnection;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    private MaterialButton play_pause_image_view;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    SpotifyConnection spotifyConnection;
    private SharedPreferences msharedPreferences;

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

        msharedPreferences = getActivity().getSharedPreferences("SPOTIFY", 0);
    }

    private void attemptStartwave(float beatFreq) {
        if(MainActivity.wave!=null){
            MainActivity.wave.release();
        }
        MainActivity.wave = new Binaural(200, beatFreq);
        if (!MainActivity.wave.getIsPlaying()) {
            MainActivity.wave.start();
            spotifyConnection.getPlayerInstance(getContext()).getPlayerApi().resume();
            play_pause_image_view.setActivated(true);
            play_pause_image_view.setChecked(true);
        } else {
            play_pause_image_view.setActivated(false);
            play_pause_image_view.setChecked(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_video, container, false);
        play_pause_image_view = inflatedView.findViewById(R.id.play_pause_image_view);
        play_pause_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spotifyConnection = new SpotifyConnection();
                if (play_pause_image_view.isChecked()) {
                    play_pause_image_view.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_play_arrow_24_white));
                    spotifyConnection.getPlayerInstance(getContext()).getPlayerApi().pause();
                    MainActivity.wave.stop();
                } else {
                    play_pause_image_view.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_round_pause_24_white));
                    float beatFreq = msharedPreferences.getFloat("beatFreq", 0.0f);
                    if (beatFreq > 0.0) {
                        attemptStartwave(beatFreq);
                    }
                    play_pause_image_view.setChecked(false);

                }
            }
        });
        return inflatedView;
    }
}