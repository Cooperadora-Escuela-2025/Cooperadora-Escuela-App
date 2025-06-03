package com.example.cooperadora_escuela.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
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
    private TextView miTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        miTextView = findViewById(R.id.tvInstructions);
        applyFontSizeIfNeeded();



        // Toolbar con flecha atrás
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

       // SwitchCompat switchTheme = findViewById(R.id.switch_accessible_theme);
        SwitchCompat switchFontSize = findViewById(R.id.switch_font_size);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchFontSize.setChecked(prefs.getBoolean("increase_font_size", false));


        switchFontSize.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("increase_font_size", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            );

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



        // Poner texto de instrucciones (podés personalizarlo)
        String instrucciones = "Bienvenido a la app!\n\n" +
                "1. Para modificar tu perfil, ve a la sección Perfil.\n" +
                "2. Para hacer reservas, ingresa a la sección Reservas.\n" +
                "3. Usa el switch para cambiar el tamaño de letra si lo necesitas.\n" +
                "4. Si tenés dudas, contactanos desde la sección Contacto.\n\n" +
                "¡Gracias por usar la app!";
        miTextView.setText(instrucciones);

    }

    @Override  // Manejar flecha atrás en Toolbar
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void  applyFontSizeIfNeeded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       // boolean isAccessible = prefs.getBoolean("accessible_theme", false);
        boolean increaseFont = prefs.getBoolean("increase_font_size", false);

        if (increaseFont) {
            float newSize = 25f;  // o el tamaño que quieras

            miTextView.setTextSize(newSize);
        }

//        if (isAccessible) {
//            setTheme(R.style.AppTheme_Accessible);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        } else {
//            setTheme(R.style.Theme_Cooperadora_escuela);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        }

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

