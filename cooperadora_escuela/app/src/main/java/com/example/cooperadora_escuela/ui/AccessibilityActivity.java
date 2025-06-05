package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.preference.PreferenceManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.AboutUsActivity;
import com.example.cooperadora_escuela.ContactActivity;
import com.example.cooperadora_escuela.HomeActivity;
import com.example.cooperadora_escuela.ProductsActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.WebActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AccessibilityActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView miTextView;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Acciones del menú
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Aca se agregan navegación a las activities
                if (id == R.id.nav_home) {
                    startActivity(new Intent(AccessibilityActivity .this, HomeActivity.class));
                } else if (id == R.id.nav_product) {
                    Intent intent = new Intent(AccessibilityActivity .this, ProductsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(AccessibilityActivity .this, ProfileActivity.class));
                } else if (id == R.id.nav_accesibilidad) {
                    Intent intent = new Intent(AccessibilityActivity .this, AccessibilityActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(AccessibilityActivity .this, ContactActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(AccessibilityActivity .this, AboutUsActivity.class));
                } else if (id == R.id.nav_web) {
                    Intent intent = new Intent(AccessibilityActivity .this, WebActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                    logoutUser(); // llamamos a salir
                    return true;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });



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
    //cerrar sesion
    private void logoutUser(){
        try{
            MasterKey masterKey=new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences= EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // borra todos los tokens
            editor.apply();

            // volver al login eliminando el historial
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Cerraste sesión con exito", Toast.LENGTH_SHORT).show();
        }catch(GeneralSecurityException | IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override  // Manejar flecha atrás en Toolbar
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

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

