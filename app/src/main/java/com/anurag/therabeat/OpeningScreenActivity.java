package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OpeningScreenActivity extends AppCompatActivity implements View.OnClickListener {

    //View Components
    Button anxietyButton;
    Button attentionButton;
    Button memoryButton;
    private String TAG = getClass().getSimpleName().toString();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);
        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        editor = this.getSharedPreferences("SPOTIFY", 0).edit();
        initializeView();

        anxietyButton.setOnClickListener(this);
        attentionButton.setOnClickListener(this);
        memoryButton.setOnClickListener(this);

        if (!mSharedPreferences.getString("token", "").equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Successfully Connected to Spotify", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void initializeView() {
        anxietyButton = findViewById(R.id.MusicChoiceButton);
        attentionButton = findViewById(R.id.AboutUsChoiceButton);
        memoryButton = findViewById(R.id.FeedbackChoiceButton);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.MusicChoiceButton:
                intent = new Intent(OpeningScreenActivity.this, UserMoodChoice.class);
                startActivity(intent);
                break;
            case R.id.AboutUsChoiceButton:
                intent = new Intent(OpeningScreenActivity.this,

                        AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.FeedbackChoiceButton:
                Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
                feedbackEmail.setType("text/email");
                feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"sampleutd@gmail.com"});
                feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Therabeat Feedback");
                startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
                break;
            default:
                break;
        }

    }
}