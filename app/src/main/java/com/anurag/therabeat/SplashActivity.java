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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName().toString();
    private SharedPreferences msharedPreferences;
    private boolean exception = false;
    private String errorMsg;
    private Boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    protected void onStart() {
        super.onStart();
        errorMsg = "";
        PackageManager pm = this.getPackageManager();
        isPackageInstalled("com.spotify.music", pm);
        isNetworkAvailable();
        if(!exception) {
            msharedPreferences = this.getSharedPreferences("Therabeat", 0);
            firstTime = msharedPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                Intent intent = new Intent(SplashActivity.this,

                        WelcomeScreen.class);
                startActivity(intent);
                destroy();
            } else {
                Intent intent;
//                    if (msharedPreferences.getFloat("beatFreq", 0.0F) == 0.0F) {
                intent = new Intent(SplashActivity.this,

                        UserMoodChoice.class);

//                    } else {
//                        intent = new Intent(LoginActivity.this,
//
//                                MainActivity.class);
//                    }
                startActivity(intent);
                destroy();
            }
        } else {
            displayError();
            if (errorMsg.contains(getResources().getString(R.string.spotifyError))) {
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
    }

    public void destroy() {

        SplashActivity.this.finish();

    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            errorMsg += getResources().getString(R.string.networkError) + "\n";
            exception = true;
        }
    }

    private void isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            errorMsg += getResources().getString(R.string.spotifyError) + "\n";
            exception = true;
        }
    }

    private void displayError() {
        TextView errorTextView = findViewById(R.id.SplashActivityErrorTextView);
        errorTextView.setText(errorMsg);
        errorTextView.setVisibility(View.VISIBLE);
    }
}