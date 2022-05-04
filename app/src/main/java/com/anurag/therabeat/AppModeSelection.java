package com.anurag.therabeat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.anurag.therabeat.Database.AnxietyUsage;
import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AppExecutors;
import com.anurag.therabeat.Database.AttentionUsage;
import com.anurag.therabeat.Database.MemoryUsage;
import com.anurag.therabeat.Database.TotalUsage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppModeSelection extends AppCompatActivity {

    Button ConnectToSpotify;
    Button UseOfflineButton;
    Button InfoButton;
    Button SettingsButton;
    String greeting = "";
    ConstraintLayout RootContainer;
    private SharedPreferences msharedPreferences;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msharedPreferences = SingletonInstances.getInstance(this.getApplicationContext()).getSharedPreferencesInstance();
        db = AppDatabase.getInstance(this.getApplicationContext());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] a = {10800, 14400, 7200, 21600, 28800, 32400, 3600};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("hello", date + " totalusage " + a[j]);
                    db.totalUsageDao().insertTotalUsage(new TotalUsage(date, a[j]));
                }
            }
        });
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] b = {14400, 21600, 7200, 14400, 28800, 3600, 32400};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("hello", date + " memoryusage " + b[j]);
                    db.memoryUsageDao().insertMemoryUsage(new MemoryUsage(date, b[j]));
                }
            }
        });
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] c = {21600, 32400, 14400, 28800, 14400, 3600, 7200};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("hello", date + " anxietyusage " + c[j]);
                    db.anxietyUsageDao().insertAnxietyUsage(new AnxietyUsage(date, c[j]));
                }
            }
        });
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                int[] d = {32400, 3600, 28800, 14400, 21600, 7200, 14400};
                for (int i = -6, j = 0; i <= 0; i++, j++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, i);
                    String date = sdf.format(calendar.getTime());
                    Log.d("date", date + " attentionusage " + d[j]);
                    db.attentionUsageDao().insertAttentionUsage(new AttentionUsage(date, d[j]));
                }
            }
        });

        setContentView(R.layout.activity_app_mode_selection);
        if (getIntent().hasExtra("pushnotification")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                } else {
                    Intent intent = new Intent(AppModeSelection.this, MusicListActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                }
            }
        }
        RootContainer = findViewById(R.id.rootContainer);

        String mode = msharedPreferences.getString("mode", "Memory");
        Log.d("mode", mode);
        switch (mode) {
            case "Memory":
                RootContainer.setBackgroundColor(Color.parseColor("#c8e6c9"));
                getWindow().setStatusBarColor(Color.parseColor("#c8e6c9"));
                break;
            case "Anxiety":
                RootContainer.setBackgroundColor(Color.parseColor("#bbdefb"));
                getWindow().setStatusBarColor(Color.parseColor("#bbdefb"));
                break;
            case "Attention":
                RootContainer.setBackgroundColor(Color.parseColor("#ffcdd2"));
                getWindow().setStatusBarColor(Color.parseColor("#ffcdd2"));
                break;
        }
        ConnectToSpotify = findViewById(R.id.ConnectToSpotify);
        UseOfflineButton = findViewById(R.id.UseOfflineButton);
        InfoButton = findViewById(R.id.InfoButton);
        SettingsButton = findViewById(R.id.SettingsButton);

        ConnectToSpotify.setOnClickListener(view -> {
            Intent intent = new Intent(AppModeSelection.this, LoginActivity.class);
            startActivity(intent);
        });

        UseOfflineButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                } else {
                    Intent intent = new Intent(AppModeSelection.this, MusicListActivity.class);
                    startActivity(intent);
                }
            }
        });

        InfoButton.setOnClickListener(view -> {
            Intent intent = new Intent(AppModeSelection.this, AboutUsActivity.class);
            startActivity(intent);
        });

        SettingsButton.setOnClickListener(view -> {
            getSupportFragmentManager()

                    .beginTransaction()
                    .addToBackStack("settings")
                    .replace(R.id.settings_fragment, new Settings())
                    .commit();
        });
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
//            Glide.with(this).asGif().load(R.raw.sunrise).listener(new RequestListener<GifDrawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
//                    resource.setLoopCount(1);
//                    resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
//                        @Override
//                        public void onAnimationEnd(Drawable drawable) {
//                            //do whatever after specified number of loops complete
//                        }
//                    });
//                    return false;
//                }
//            }).into((ImageView) findViewById(R.id.TimeOfDayGIF));
            Glide.with(this).load(R.raw.afternoon).into(new DrawableImageViewTarget(findViewById(R.id.TimeOfDayGIF)));
        }
        TextView txt = (TextView) findViewById(R.id.WelcomeTextView);
        txt.setText(greeting);


    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AppModeSelection.this, MusicListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Read Storage Permission is required", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        else {
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setTitle("Quit Therabeat")
                    .setMessage("Are you sure you want to exit?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    }

    public void destroy() {

        AppModeSelection.this.finish();

    }
}