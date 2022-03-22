package com.anurag.therabeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.anurag.therabeat.connectors.SpotifyConnection;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SpotifyConnection spotifyConnection = new SpotifyConnection(this);
        SpotifyAppRemote mSpotifyAppRemote = spotifyConnection.mSpotifyAppRemote;
        String greeting;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        AppUsageFragment appUsageFragment = new AppUsageFragment();
        FrameLayout cardView = findViewById(R.id.testFrame);
        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame,appUsageFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame1,new HomeFragment()).commit();
//        cardView.addView(appUsageFragment.getView());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour>= 12 && hour < 17){
            greeting = "Good Afternoon";
        } else if(hour >= 17 && hour < 24){
            greeting = "Good Evening";
        } else {
            greeting = "Good Morning";
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(greeting);
        setSupportActionBar(toolbar);

    }
}