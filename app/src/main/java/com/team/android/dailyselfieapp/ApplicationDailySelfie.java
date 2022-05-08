package com.team.android.dailyselfieapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ApplicationDailySelfie extends Application {
    private static int started = 0;
    private static int stopped = 0;

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                ++started;
                Log.e(Constains.TAG, "One Activity Started.");
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                ++stopped;
                Log.e(Constains.TAG, "One Activity Stopped.");

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e(Constains.TAG, "One Activity Destroy");
                startService(new Intent(getApplicationContext(), NotificationService.class));

            }
        });
    }

}
