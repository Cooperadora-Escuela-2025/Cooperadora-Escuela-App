package com.example.cooperadora_escuela;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;


import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAccessible = prefs.getBoolean("accessible_theme", false); // coincide con la clave

        boolean increaseFont = prefs.getBoolean("increase_font_size", false);

        if (isAccessible){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        if (increaseFont){
            setTheme(R.style.AppTheme_Accessible_FontLarge);
        } else {
            setTheme(R.style.Theme_Cooperadora_escuela);
        }
    }
}
