package com.anurag.therabeat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
        hideSystemUI();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        adjustFontScale(getResources().getConfiguration());

    }

    public void adjustFontScale(Configuration configuration) {
        Log.d("font", String.valueOf(configuration.fontScale));
        if (configuration.fontScale > 1.30) {
            Log.d("font", String.valueOf(configuration.fontScale));
            configuration.fontScale = 1.30f;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        errorMsg = "";
        PackageManager pm = this.getPackageManager();
        isPackageInstalled("com.spotify.music", pm);
        isNetworkAvailable();
        if (!exception) {
            msharedPreferences = this.getSharedPreferences("Therabeat", 0);
            firstTime = msharedPreferences.getBoolean("firstTime", true);
//            if (firstTime) {
//                Intent intent = new Intent(SplashActivity.this,
//
//                        WelcomeScreen.class);
//                startActivity(intent);
//                destroy();
//            } else {
            Intent intent;
//                    if (msharedPreferences.getFloat("beatFreq", 0.0F) == 0.0F) {
            intent = new Intent(SplashActivity.this,

                    OpeningScreenActivity.class);

//                    } else {
//                        intent = new Intent(LoginActivity.this,
//
//                                MainActivity.class);
//                    }
            if (getIntent().getExtras() != null) {
                Bundle bundle = getIntent().getExtras();
                Log.d("value", bundle.getString("pushnotification"));
                intent.putExtra("pushnotification", "true");
            }
            startActivity(intent);
            destroy();
//            }
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

    public void hideSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void displayError() {
        TextView errorTextView = findViewById(R.id.SplashActivityErrorTextView);
        errorTextView.setText(errorMsg);
        errorTextView.setVisibility(View.VISIBLE);
    }
}