package com.anurag.therabeat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anurag.therabeat.AudioListAdapter;
import com.anurag.therabeat.AudioModel;
import com.anurag.therabeat.R;
import com.sevenshifts.sideheaderdecorator.SideHeaderDecorator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    RecyclerView audioListView;
    AudioListAdapter audioListAdapter;
    Context context;
    String greeting;
    List<AudioModel> allAudioFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);


        //Checks permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }

        //Finds all the mp3 files in device and makes a list
        getSongs();

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
        } else if (hour >= 16 && hour < 24) {
            greeting = "Good Evening";
        } else {
            greeting = "Good Morning";
        }

        Toolbar toolbar = findViewById(R.id.actualtoolbar);
        toolbar.setTitle(greeting);

        setSupportActionBar(toolbar);


    }

    public void getSongs(){
        context = MusicListActivity.this;
        audioListView = findViewById(R.id.audioListView);

        allAudioFiles = getAllAudioFromDevice(context);

        audioListAdapter = new AudioListAdapter(context,allAudioFiles);
        audioListView.setLayoutManager(new LinearLayoutManager(context));
        audioListView.setAdapter(audioListAdapter);
        SideHeaderDecorator.HeaderProvider headerProvider = new SideHeaderDecorator.HeaderProvider() {
            @Override
            public Object getHeader(int i) {
                return allAudioFiles.get(i).getaAlbum();
            }
        };

        SideHeaderDecorator sideHeaderDecorator = new SideHeaderDecorator(headerProvider) {
            @NonNull
            @Override
            public View getHeaderView(Object o, @NonNull RecyclerView recyclerView) {
                TextView textView = (TextView) LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.header_view,recyclerView,false);
                textView.setText(o.toString());

                return textView;
            }
        };
        audioListView.addItemDecoration(sideHeaderDecorator);
    }

    public List<AudioModel> getAllAudioFromDevice(final Context context) {

        final List<AudioModel> tempAudioList = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0 ";
//                + "AND " + MediaStore.Audio.Media.DURATION + ">= 60000";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String title = cursor.getString(22);
                if(!title.contains(".aac")){
                    String album = cursor.getString(34);
                    String artist = cursor.getString(9);

                    // Save to audioList
                    AudioModel audioModel = new AudioModel();
                    audioModel.setaName(title);
                    audioModel.setaAlbum(album);
                    audioModel.setaArtist(artist);
                    audioModel.setaPath(path);
                    tempAudioList.add(audioModel);
                }
            }
        }

        return tempAudioList;
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSongs();
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}