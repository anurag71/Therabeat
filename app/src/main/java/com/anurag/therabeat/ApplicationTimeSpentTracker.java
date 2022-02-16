package com.anurag.therabeat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ApplicationTimeSpentTracker implements Application.ActivityLifecycleCallbacks {
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private long startingTime;
    private long stopTime;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {

            // App enters foreground
            startingTime = System.currentTimeMillis() / 1000; //This is the starting time when the app began to start
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {

            // App enters background
            stopTime = System.currentTimeMillis() / 1000;//This is the ending time when the app is stopped

            long totalSpentTime = stopTime - startingTime; //This is the total spent time of the app in foreground
            activity.getSharedPreferences("Therabeat", 0).edit().putLong("TimeSpent", totalSpentTime).apply();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
