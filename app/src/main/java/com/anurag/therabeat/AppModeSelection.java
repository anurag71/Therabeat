package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.util.Calendar;
import java.util.Date;

public class AppModeSelection extends AppCompatActivity {

    Button ConnectToSpotify;
    Button UseOfflineButton;
    String greeting = "";
    ConstraintLayout RootContainer;
    private SharedPreferences msharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_mode_selection);
        RootContainer = findViewById(R.id.rootContainer);
        msharedPreferences = SingletonInstances.getInstance(this.getApplicationContext()).getSharedPreferencesInstance();
        String mode = msharedPreferences.getString("mode", "");
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
            Glide.with(this).asGif().load(R.raw.sunrise).listener(new RequestListener<GifDrawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                    resource.setLoopCount(1);
                    resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            //do whatever after specified number of loops complete
                        }
                    });
                    return false;
                }
            }).into((ImageView) findViewById(R.id.TimeOfDayGIF));
        }
        TextView txt = (TextView) findViewById(R.id.WelcomeTextView);
        txt.setText(greeting);


    }

    public void destroy() {

        AppModeSelection.this.finish();

    }
}