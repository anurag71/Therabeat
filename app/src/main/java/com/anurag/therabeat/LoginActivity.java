package com.anurag.therabeat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.anurag.therabeat.connectors.UserService;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {

    SpotifyConnection spotifyConnection;
    PlaylistService playlistService;
    String playlistId;
    AlertDialog.Builder alertDialog;
    //Class Variables
    private String TAG = getClass().getSimpleName().toString();
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        alertDialog = new AlertDialog.Builder(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playlistService = new PlaylistService(this);
        spotifyConnection = new SpotifyConnection(this);
        spotifyConnection.openLoginWindow(this);
        msharedPreferences = this.getSharedPreferences("Therabeat", 0);
        playlistId = msharedPreferences.getString("playlistId", "");
    }

    @Override

    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1337) {

            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {

                // Response was successful and contains auth token

                case TOKEN:
                    editor = getSharedPreferences("Therabeat", 0).edit();
                    Log.d(TAG, response.getAccessToken());
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    Log.d(TAG, playlistId);
//                    if (playlistId.equals("")) {
                        waitForUserInfo();
//                    }
                    Intent intent;
//                    if (msharedPreferences.getFloat("beatFreq", 0.0F) == 0.0F) {
                    intent = new Intent(LoginActivity.this,

                            MainActivity2.class);
                    startActivity(intent);
                    destroy();

                    break;

                // Auth flow returned an error

                case ERROR:
                    Log.e(TAG, "Auth error: " + response.getError());
                    alertDialog
                            .setTitle("Error")
                            .setMessage(response.getError())

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
//                                    editor.putBoolean("firstTime",false);
//                                    editor.apply();
//                                    Intent intent = new Intent(WelcomeScreen.this,
//
//                                            LoginActivity.class);
//                                    startActivity(intent);
//
//                                    destroy();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    break;

                // Most likely auth flow was cancelled

                default:
//                    Log.d(TAG, "Auth result: " + response.getType());
//                    alertDialog
//                            .setTitle("Error")
//                            .setMessage(response.getError())
//
//                            // Specifying a listener allows you to take an action before dismissing the dialog.
//                            // The dialog is automatically dismissed when a dialog button is clicked.
//                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
                    // Continue with delete operation
//                                    editor.putBoolean("firstTime",false);
//                                    editor.apply();
//                                    Intent intent = new Intent(WelcomeScreen.this,
//
//                                            LoginActivity.class);
//                                    startActivity(intent);
//
//                                    destroy();
//                                }
//                            })
//
//                            // A null listener allows the button to dismiss the dialog and take no further action.
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();

            }

        }

    }

    public void destroy() {

        LoginActivity.this.finish();

    }

    private void waitForUserInfo() {
        UserService userService = new UserService(SingletonInstances.getInstance(this.getApplicationContext()).getRequestQueue(), msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            userId = user.id;
            Log.d(TAG, userId);
            playlistService.getPlaylists(userId, this, editor);
        });
    }
}