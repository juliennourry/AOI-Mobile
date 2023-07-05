package fr.java.aoitechnicien.Service;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.java.aoitechnicien.Function.SharedHelper;
import fr.java.aoitechnicien.HomeActivity;
import fr.java.aoitechnicien.R;

public class LifeTime extends Application {
    private static boolean isAppInForeground;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new AppLifecycleCallbacks());
    }

    public static boolean isAppInForeground() {
        return isAppInForeground;
    }

    private static class AppLifecycleCallbacks implements ActivityLifecycleCallbacks {

        private int activeActivities = 0;

        @Override
        public void onActivityStarted(Activity activity) {
            if (activeActivities == 0) {
                isAppInForeground = true;
                Log.e("DEBUG_LIFETIME", "FOREGROUND");
            }
            activeActivities++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activeActivities--;
            if (activeActivities == 0) {
                isAppInForeground = false;
                Log.e("DEBUG_LIFETIME", "BACKGROUND");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }
    }
}
