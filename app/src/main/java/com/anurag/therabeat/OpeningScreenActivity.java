package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.util.Calendar;
import java.util.Date;

public class OpeningScreenActivity extends AppCompatActivity implements View.OnClickListener {

    String greeting = "";

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

        String TimeGIF = "";
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
            Glide.with(this).load(R.raw.afternoon).into(new DrawableImageViewTarget(findViewById(R.id.TimeOfDayGIF)));
        } else if (hour >= 16 && hour < 24) {
            greeting = "Good Evening";
            Glide.with(this).load(R.raw.evening).into(new DrawableImageViewTarget(findViewById(R.id.TimeOfDayGIF)));
        } else {
            greeting = "Good Morning";
            Glide.with(this).load(R.raw.afternoon).into(new DrawableImageViewTarget(findViewById(R.id.TimeOfDayGIF)));
        }
        TextView txt = (TextView) findViewById(R.id.WelcomeTextView);
        txt.setText(greeting);

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
                intent = new Intent(OpeningScreenActivity.this, AppModeSelection.class);
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