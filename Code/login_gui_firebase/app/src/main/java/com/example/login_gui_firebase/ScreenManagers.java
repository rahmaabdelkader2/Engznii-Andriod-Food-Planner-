package com.example.login_gui_firebase;

import android.content.Context;
import android.content.SharedPreferences;

public class ScreenManagers {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String PREF_FIRST_TIME = "isFirstTime";

    private SharedPreferences preferences;

    public ScreenManagers(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(PREF_FIRST_TIME, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_FIRST_TIME, isFirstTime);
        editor.apply();
    }
}