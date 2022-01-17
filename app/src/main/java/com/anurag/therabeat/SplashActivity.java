package com.anurag.therabeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anurag.therabeat.connectors.SpotifyConnection;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SplashActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    SpotifyConnection spotifyConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spotifyConnection = new SpotifyConnection();
        spotifyConnection.openLoginWindow(this);
        PackageManager pm = this.getPackageManager();
        boolean isInstalled = isPackageInstalled("com.spotify.music", pm);
        if(isInstalled){
            spotifyConnection.openLoginWindow(this);
            msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        }
        else{
            TextView spotifyError = findViewById(R.id.spotifyErrorTextView);
            Button playstoreRedirect = findViewById(R.id.playstoreRedirect);
            spotifyError.setVisibility(View.VISIBLE);
            playstoreRedirect.setVisibility(View.VISIBLE);

            playstoreRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String appPackageName = "com.spotify.music"; // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
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

                    Log.i("SplashActivity","Auth token: " + response.getAccessToken());

                    Intent intent = new Intent(SplashActivity.this,

                            MainActivity.class);
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    startActivity(intent);

                    destroy();

                    break;

                // Auth flow returned an error

                case ERROR:

                    Log.e("SplashActivity","Auth error: " + response.getError());

                    break;

                // Most likely auth flow was cancelled

                default:

                    Log.d("SplashActivity","Auth result: " + response.getType());

            }

        }

    }

    public void destroy(){

        SplashActivity.this.finish();

    }
}