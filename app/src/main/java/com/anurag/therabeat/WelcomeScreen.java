package com.anurag.therabeat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreen extends AppCompatActivity {

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        editor = this.getSharedPreferences("Therabeat", 0).edit();
        Button SpotifyButton = findViewById(R.id.SpotifyConnectButton);
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        SpotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog
                        .setTitle("Information")
                        .setMessage("Therabeat will create a playlist on your behalf on Spotify. You can disconnect your spotify account at any time from the in-app settings.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                editor.putBoolean("firstTime", false);
                                editor.apply();
                                Intent intent = new Intent(WelcomeScreen.this,

                                        OpeningScreenActivity.class);
                                startActivity(intent);

                                destroy();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

                // Create the AlertDialog object and return it
            }
        });
    }

    public void destroy() {

        WelcomeScreen.this.finish();

    }
}