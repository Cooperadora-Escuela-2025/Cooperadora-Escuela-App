package com.example.cooperadora_escuela.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AccessibilityActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeFromPrefs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        // Toolbar con flecha atrás
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SwitchCompat switchTheme = findViewById(R.id.switch_accessible_theme);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchTheme.setChecked(prefs.getBoolean("accessible_theme", false));


        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("accessible_theme", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            );

            Toast.makeText(
                    this,
                    "Para aplicar completamente el tema, por favor reiniciá la aplicación.",
                    Toast.LENGTH_LONG
            ).show();
            recreate();  // reiniciar  para que tome el cambio
        });

//        SwitchCompat switchFontSize = findViewById(R.id.switch_increase_font_size);
//        switchFontSize.setChecked(prefs.getBoolean("increase_font_size", false));
//
//        switchFontSize.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            prefs.edit().putBoolean("increase_font_size", isChecked).apply();
//            Toast.makeText(
//                    this,
//                    "Para aplicar el tamaño de fuente, por favor reiniciá la aplicación.",
//                    Toast.LENGTH_LONG
//            ).show();
//            recreate();
//        });

    }

    @Override  // Manejar flecha atrás en Toolbar
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void applyThemeFromPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAccessible = prefs.getBoolean("accessible_theme", false);
        boolean increaseFont = prefs.getBoolean("increase_font_size", false);

        if (isAccessible) {
            setTheme(R.style.AppTheme_Accessible);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            setTheme(R.style.Theme_Cooperadora_escuela);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

//
//        if (increaseFont) {
//            setTheme(R.style.AppTheme_Accessible_FontLarge);
//        } else if (isAccessible) {
//            setTheme(R.style.AppTheme_Accessible);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        } else {
//            setTheme(R.style.Theme_Cooperadora_escuela);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        }
    }

}

