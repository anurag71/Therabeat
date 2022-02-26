package com.anurag.therabeat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class SampleForegroundService extends Service {

    final String ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_STOP)) {
            stopSelf();
        }

        return START_NOT_STICKY;
    }
}
