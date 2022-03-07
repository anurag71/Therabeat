package com.anurag.therabeat;

//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class WelcomeScreen extends AppCompatActivity {
//
//    SharedPreferences.Editor editor;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome_screen);
//        editor = this.getSharedPreferences("Therabeat", 0).edit();
//        Button SpotifyButton = findViewById(R.id.SpotifyConnectButton);
//
//        SpotifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog
//                        .setTitle("Information")
//                        .setMessage("Therabeat will create a playlist on your behalf on Spotify. You can disconnect your spotify account at any time from the in-app settings.")
//
//                        // Specifying a listener allows you to take an action before dismissing the dialog.
//                        // The dialog is automatically dismissed when a dialog button is clicked.
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Continue with delete operation
//                                editor.putBoolean("firstTime", false);
//                                editor.apply();
//                                Intent intent = new Intent(WelcomeScreen.this,
//
//                                        OpeningScreenActivity.class);
//                                startActivity(intent);
//
//                                destroy();
//                            }
//                        })
//
//                        // A null listener allows the button to dismiss the dialog and take no further action.
//                        .setIcon(android.R.drawable.ic_dialog_info)
//                        .show();
//
//                // Create the AlertDialog object and return it
//            }
//        });
//    }
//
//    public void destroy() {
//
//        WelcomeScreen.this.finish();
//
//    }
//}

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;


public class WelcomeScreen extends AppIntro {

    SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editor = this.getSharedPreferences("Therabeat", 0).edit();

        addSlide(AppIntroFragment.newInstance(
                "Landing page",
                "Get into music.\nGet to know more about Therabeat\nProvide us with you valuable feedback",
                R.drawable.landing_page,
                ContextCompat.getColor(this, R.color.primary_blue),
                Color.WHITE,
                Color.WHITE
        ));

        addSlide(AppIntroFragment.newInstance(
                "Choose Cognitive Assistance",
                "Choose from the offered cognitive modes by clicking the respective buttons",
                R.drawable.cognitive_page,
                ContextCompat.getColor(this, R.color.primary_blue),
                Color.WHITE,
                Color.WHITE
        ));

        addSlide(AppIntroFragment.newInstance(
                "Home Screen",
                "View your saved songs and click them for quickly playing music and binaural beat.\nFor the first time please swipe down explicitly",
                R.drawable.home_page,
                ContextCompat.getColor(this, R.color.primary_blue),
                Color.WHITE,
                Color.WHITE
        ));

        addSlide(AppIntroFragment.newInstance(
                "Search Songs",
                "Search for your favorite songs and play them them directly via Spotify" + Html.fromHtml("<sup>*</sup>") + "\nAdd them into your playlist by clicking on the overflow menu" + Html.fromHtml("<br/><br/><sup>*</sup>Playing specific songs on Spotify requires Spotify Premium"),
                R.drawable.search_songs,
                ContextCompat.getColor(this, R.color.primary_blue),
                Color.WHITE,
                Color.WHITE
        ));

        addSlide(AppIntroFragment.newInstance(
                "Track Your Usage Analytics",
                "Keep track of how long you have listened on the app to monitor improvements, etc.\nTo obtain accurate usage, please manually pause the music by clicking on the pause button in the Music Player",
                R.drawable.analytics_page,
                ContextCompat.getColor(this, R.color.primary_blue),
                Color.WHITE,
                Color.WHITE
        ));


        addSlide(AppIntroCustomLayoutFragment.newInstance(
                R.layout.music_player_layout
        ));

//        ImageView imageView = findViewById(R.id.musicplayerimageView);
//
//        /*from raw folder*/
//        Glide.with(this)
//                .load(R.raw.music_player)
//                .into(imageView);

        addSlide(AppIntroCustomLayoutFragment.newInstance(
                R.layout.refresh_layout
        ));

        // Fade Transition
        setTransformer(AppIntroPageTransformerType.Zoom.INSTANCE);

        //Speed up or down scrolling
        setScrollDurationFactor(1);

        //Enable the color "fade" animation between two slides (make sure the slide implements SlideBackgroundColorHolder)
//        setColorTransitionsEnabled(true);

        //Prevent the back button from exiting the slides
        setSystemBackButtonLocked(true);

        //Activate wizard mode (Some aesthetic changes)
        setWizardMode(true);

        //Enable/disable immersive mode (no status and nav bar)
        setImmersive(true);

        //Enable/disable page indicators
        setIndicatorEnabled(true);

        //Dhow/hide ALL buttons
        setButtonsEnabled(true);
        setVibrate(true);
        setVibrateDuration(50L);
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        editor.putBoolean("firstTime", false);
        editor.apply();
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
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
                        finish();
                    }
                })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
    }
}