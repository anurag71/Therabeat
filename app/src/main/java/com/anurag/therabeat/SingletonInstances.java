package com.anurag.therabeat;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.Database.AppDatabase;

public class SingletonInstances {
    private static SingletonInstances instance;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    AppDatabase db;
    private static Context ctx;

    private SingletonInstances(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
        sharedPreferences = getSharedPreferencesInstance();
        db = getDbInstance();
    }

    public static synchronized SingletonInstances getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonInstances(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public SharedPreferences getSharedPreferencesInstance() {
        if (sharedPreferences == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            sharedPreferences = ctx.getSharedPreferences("Therabeat", 0);
        }
        return sharedPreferences;
    }

    public AppDatabase getDbInstance() {
        if (db == null) {
            db = Room.databaseBuilder(ctx,
                    AppDatabase.class, "AppUsageHistory_database").allowMainThreadQueries().build();
        }
        return db;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}