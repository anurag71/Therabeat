package com.anurag.therabeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class AppModeSelection extends AppCompatActivity {

    Button ConnectToSpotify;
    Button UseOfflineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_mode_selection);

        ConnectToSpotify = findViewById(R.id.ConnectToSpotify);
        UseOfflineButton = findViewById(R.id.UseOfflineButton);

        ConnectToSpotify.setOnClickListener(view -> {
            Intent intent = new Intent(AppModeSelection.this, LoginActivity.class);
            startActivity(intent);
            destroy();
        });

        UseOfflineButton.setOnClickListener(view -> {
            Intent intent = new Intent(AppModeSelection.this, MusicListActivity.class);
            startActivity(intent);
            destroy();
        });

    }

    public void destroy() {

        AppModeSelection.this.finish();

    }
}