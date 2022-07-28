package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class UserMoodChoice extends AppCompatActivity implements View.OnClickListener {

    //Class Variables
    private String TAG = getClass().getSimpleName().toString();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    //View Components
    Button anxietyButton;
    Button attentionButton;
    Button memoryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mSharedPreferences = this.getSharedPreferences("Therabeat", 0);
        editor = this.getSharedPreferences("Therabeat", 0).edit();
        setContentView(R.layout.activity_user_mood_choice);
        initializeView();

        anxietyButton.setOnClickListener(this);
        attentionButton.setOnClickListener(this);
        memoryButton.setOnClickListener(this);

        if (!mSharedPreferences.getString("token", "").equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Successfully Connected to Spotify", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void initializeView(){
        anxietyButton = findViewById(R.id.MusicChoiceButton);
        attentionButton = findViewById(R.id.AboutUsChoiceButton);
        memoryButton = findViewById(R.id.FeedbackChoiceButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MusicChoiceButton:
                Log.d(TAG, "AnxietyUsage Button pressed");
                editor.putFloat("beatFreq", 4.00F);
                editor.putString("mode", "Anxiety");
                break;
            case R.id.AboutUsChoiceButton:
                Log.d(TAG, "AttentionUsage Button pressed");
                editor.putFloat("beatFreq", 6.00F);
                editor.putString("mode", "Attention");
                break;
            case R.id.FeedbackChoiceButton:
                Log.d(TAG, "MemoryUsage Button pressed");
                editor.putFloat("beatFreq", 19.00F);
                editor.putString("mode", "Memory");
                break;
            default:
                break;
        }
        editor.apply();
        Intent intent = new Intent(UserMoodChoice.this,

                AppModeSelection.class);
        startActivity(intent);
    }
}