package com.anurag.therabeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        mSharedPreferences = this.getSharedPreferences("SPOTIFY",0);
        editor = this.getSharedPreferences("SPOTIFY", 0).edit();
        setContentView(R.layout.activity_user_mood_choice);
        initializeView();

        anxietyButton.setOnClickListener(this);
        attentionButton.setOnClickListener(this);
        memoryButton.setOnClickListener(this);

        if(!mSharedPreferences.getString("token", "").equals("")){
            Toast toast = Toast.makeText(getApplicationContext(),"Successfully Connected to Spotify",Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void initializeView(){
        anxietyButton = findViewById(R.id.anxietyChoiceButton);
        attentionButton = findViewById(R.id.attentionChoiceButton);
        memoryButton = findViewById(R.id.memoryChoiceButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.anxietyChoiceButton:
                Log.d(TAG,"Anxiety Button pressed");
                editor.putFloat("beatFreq", 4.00F);
                break;
            case R.id.attentionChoiceButton:
                Log.d(TAG,"Attention Button pressed");
                editor.putFloat("beatFreq", 6.00F);
                break;
            case R.id.memoryChoiceButton:
                Log.d(TAG,"Memory Button pressed");
                editor.putFloat("beatFreq", 19.00F);
                break;
            default:
                break;
        }
        editor.apply();
        Intent intent = new Intent(UserMoodChoice.this, MainActivity.class);
        startActivity(intent);
    }
}