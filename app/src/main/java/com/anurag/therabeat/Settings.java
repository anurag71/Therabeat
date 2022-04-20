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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Settings extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.activity_settings, rootKey);
        SwitchPreferenceCompat analytics = findPreference("notifications_analytics");
        analytics.setChecked(getPreferenceManager().getSharedPreferences().getBoolean("notifications_analytics", false));
        analytics.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((Boolean) newValue) {
                FirebaseMessaging.getInstance().subscribeToTopic("UsageReminders")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Successfully Subscribed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("UsageReminders")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Successfully Unsubscribed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            return true;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.white));
        return view;
    }
}