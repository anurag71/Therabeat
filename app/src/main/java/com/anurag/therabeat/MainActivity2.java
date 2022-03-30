package com.anurag.therabeat;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import com.anurag.therabeat.connectors.SpotifyConnection;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.Calendar;
import java.util.Date;


public class MainActivity2 extends AppCompatActivity {


    SearchView searchView;
    Context context;
    ConstraintLayout mainLayout;
    FrameLayout searchLayout;
    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        mainLayout = findViewById(R.id.mainLayout);
        searchLayout = findViewById(R.id.searchFrame);
        SpotifyConnection spotifyConnection = new SpotifyConnection(this);
        SpotifyAppRemote mSpotifyAppRemote = spotifyConnection.mSpotifyAppRemote;
        String greeting;
        context = this;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        AppUsageFragment appUsageFragment = new AppUsageFragment();
        FrameLayout cardView = findViewById(R.id.testFrame);
        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame, appUsageFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.testFrame1,new HomeFragment()).commit();
//        cardView.addView(appUsageFragment.getView());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
        } else if (hour >= 16 && hour < 24) {
            greeting = "Good Evening";
        } else {
            greeting = "Good Morning";
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(greeting);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    mainLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    searchView.onActionViewCollapsed();
                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                    mainLayout.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                    searchFragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.searchFrame, searchFragment).commit();
                }
            });
            EditText searchPlate = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchPlate.setHint("Enter song name");
            View searchPlateView = searchView.findViewById(R.id.search_plate);
//            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    searchFragment.setData(query);
                    Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    searchFragment.setData(newText);
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            mainLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            searchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

}