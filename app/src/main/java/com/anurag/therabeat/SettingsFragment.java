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

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.spotify.sdk.android.auth.AuthorizationClient;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        Preference logoutOption = findPreference("logout");

        logoutOption.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Therabeat", 0).edit();
                editor.remove("firstTime");
                editor.remove("playlistId");
                editor.remove("token");
                editor.commit();
                AuthorizationClient.clearCookies(getContext());
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
                return false;
            }
        });
    }
}