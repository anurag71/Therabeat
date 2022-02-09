package com.anurag.therabeat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anurag.therabeat.connectors.PlaylistService;
import com.anurag.therabeat.connectors.SpotifyConnection;
import com.anurag.therabeat.connectors.UserService;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SplashActivity extends AppCompatActivity {

    //Class Variables
    private String TAG = getClass().getSimpleName().toString();
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    SpotifyConnection spotifyConnection;
    private boolean exception = false;
    private String errorMsg;
    PlaylistService playlistService;
    String playlistId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playlistService = new PlaylistService(this);
        errorMsg = "";
        spotifyConnection = new SpotifyConnection(this);
        PackageManager pm = this.getPackageManager();
        isPackageInstalled("com.spotify.music", pm);
        isNetworkAvailable();
        if(!exception){
            spotifyConnection.openLoginWindow(this);
            msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
            playlistId = msharedPreferences.getString("playlistId", "");
        }
        else{
            displayError();
            Button playstoreRedirect = findViewById(R.id.playstoreRedirect);
            playstoreRedirect.setVisibility(View.VISIBLE);
            playstoreRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String appPackageName = "com.spotify.music"; // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }
    }

    private void isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            errorMsg+=getResources().getString(R.string.spotifyError)+"\n";
            exception=true;
        }
    }

    @Override

    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1337)

        {

            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {

                // Response was successful and contains auth token

                case TOKEN:

                    Intent intent = new Intent(SplashActivity.this,

                            UserMoodChoice.class);
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    Log.d(TAG, playlistId);
                    if (playlistId.equals("")) {
                        waitForUserInfo();
                    }

                    startActivity(intent);

                    destroy();

                    break;

                // Auth flow returned an error

                case ERROR:
                    Log.e(TAG,"Auth error: " + response.getError());
                    errorMsg+= response.getError();
                    displayError();
                    break;

                // Most likely auth flow was cancelled

                default:
                    Log.d(TAG,"Auth result: " + response.getType());
                    errorMsg+= response.getError();
                    displayError();

            }

        }

    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())){
            errorMsg+=getResources().getString(R.string.networkError)+"\n";
            exception=true;
        }
    }

    private void displayError(){
        TextView errorTextView = findViewById(R.id.SplashActivityErrorTextView);
        errorTextView.setText(errorMsg);
        errorTextView.setVisibility(View.VISIBLE);
    }

    public void destroy() {

        SplashActivity.this.finish();

    }

    private void waitForUserInfo() {
        UserService userService = new UserService(SingletonInstances.getInstance(this.getApplicationContext()).getRequestQueue(), msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            userId = user.id;
            playlistService.createPlaylist(userId, this, editor);

        });
    }
}