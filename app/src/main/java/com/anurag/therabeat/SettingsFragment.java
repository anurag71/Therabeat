package com.anurag.therabeat;//package com.anurag.therabeat;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class SettingsFragment extends AppCompatActivity {
//
//    private EditText frequencyCarrierInput;
//    private EditText frequencyBeatInput;
//    private TextView displayCarrierFrequency;
//    private Toolbar toolbar;
//    private Button saveBtn;
//    private Button backBtn;
//
//    private boolean isDataChanged = true, isCarrierValid = true, isBeatValid = true;
//
//    private SharedPreferences.Editor editor;
//    private SharedPreferences msharedPreferences;
//
//    TextWatcher textWatcherBeat = new TextWatcher() {
//
//        public void afterTextChanged(Editable s)
//        {
//            isBeatValid = isDataChanged = false;
//            if( frequencyBeatInput.getText().toString().length() == 0 )
//                frequencyBeatInput.setError( "Beat Frequency is required!" );
//            else {
//                float carrierFrequency;
//                try {
//                    carrierFrequency= Float.parseFloat(frequencyBeatInput.getText().toString());
//                    double minVal = 0;
//                    if (carrierFrequency < minVal)
//                        frequencyBeatInput.setError(String.format("Beat Frequency must be greater than %d1!", minVal ));
//                    else if (carrierFrequency > 1200)
//                        frequencyBeatInput.setError( "Beat Frequency must be less than 1200!" );
//                    else {
//                        isBeatValid = true;
//                        if (isCarrierValid)
//                        {
//                            isDataChanged = true;
//                        }
//                    }
//                } catch (NumberFormatException ex)
//                {
//                    frequencyBeatInput.setError( "Beat Frequency is required!" );
//                }
//            }
//        }
//
//        public void beforeTextChanged(CharSequence s, int start,
//                                      int count, int after) {
//        }
//
//        public void onTextChanged(CharSequence s, int start,
//                                  int before, int count) {
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_settings);
//
//        ActionBar ab = getSupportActionBar();
//
//        // Enable the Up button
//        ab.setDisplayHomeAsUpEnabled(true);
//		frequencyBeatInput = (EditText) findViewById(R.id.etBeatFrequency);
//		saveBtn = findViewById(R.id.saveBtn);
//
//        msharedPreferences = this.getSharedPreferences("Therabeat", 0);
//        editor = getSharedPreferences("Therabeat", 0).edit();
//        frequencyBeatInput.addTextChangedListener(textWatcherBeat);
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                float beatFrequency;
//
//                if(isDataChanged){
//                    beatFrequency = Float.parseFloat(frequencyBeatInput.getText().toString());
//
//                    editor.putFloat("beatFreq", beatFrequency);
//                    if(editor.commit()){
//                        Toast toast = Toast.makeText(getApplicationContext(),"Settings have been saved",Toast.LENGTH_SHORT);
//                        toast.show();
//                    }
//                }
//            }
//        });
//    }
//
//
//
//}

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.anurag.therabeat.connectors.SpotifyConnection;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        appUsageDao = SingletonInstances.getInstance(getActivity().getApplicationContext()).getDbInstance().appUsageDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        CardView logout = view.findViewById(R.id.logoutCard);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Therabeat", 0).edit();
                editor.remove("firstTime");
                editor.remove("playlistId");
                editor.remove("token");
                editor.putBoolean("isPlaying", false);
                editor.apply();
                SpotifyConnection spotifyConnection = new SpotifyConnection(getActivity().getApplicationContext());
                spotifyConnection.getPlayerInstance(getActivity());
                spotifyConnection.mSpotifyAppRemote.getPlayerApi().pause();
                MainActivity.wave.release();
                SpotifyAppRemote.disconnect(spotifyConnection.mSpotifyAppRemote);

                AuthorizationClient.clearCookies(getContext());
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}

//        if (countingPreference != null) {
//            countingPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
//                @Override
//                public CharSequence provideSummary(EditTextPreference preference) {
//                    String text = Long.toString(getActivity().getSharedPreferences("Therabeat", 0).getLong("timeListened", (long) 0.0));
//                    return text;
//                }
//            });
//        }