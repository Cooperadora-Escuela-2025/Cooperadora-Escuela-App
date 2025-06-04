package com.example.cooperadora_escuela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.ui.AccessibilityActivity;
import com.example.cooperadora_escuela.ui.ActivitiesActivity;
import com.example.cooperadora_escuela.ui.DashboardActivity;
import com.example.cooperadora_escuela.ui.LoginActivity;
import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    //private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configurar Toolbar
        setupToolbar();

        // Configurar botones
        setupButtons();


        SwitchCompat switchFontSize = findViewById(R.id.switch_font_size);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        switchFontSize.setChecked(prefs.getBoolean("increase_font_size", false));

        switchFontSize.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("increase_font_size", isChecked).apply();
            recreate();  // para que la Activity se recargue y aplique el tamaño
        });

        applyFontSizeIfNeeded();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        // configurar el toolbar como actionBar
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // referencias para el menu
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        // crear y sincroniza el toggle de la hamburguesa
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // cambiar color del ícono hamburguesa a blanco
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        // items del menú lateral de menu_drawer
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Aca se agregan navegación a las activities
                if (id == R.id.nav_home) {
                } else if (id == R.id.nav_product) {
                    Intent intent = new Intent(HomeActivity.this, ProductsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.action_cart) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                } else if (id == R.id.nav_accesibilidad) {
                    Intent intent = new Intent(HomeActivity.this, AccessibilityActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
                } else if (id == R.id.nav_web) {
                    Intent intent = new Intent(HomeActivity.this, WebActivity.class);
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
        //fin menu lateral
    }


    //botones de las tarjetas del home
    private void setupButtons() {
        // Configurar listeners para los botones
        Button btnActivities = findViewById(R.id.btn_activities);
        Button btnEvents = findViewById(R.id.btn_events);
        Button btnNew1 = findViewById(R.id.btn_new1);
        Button btnNew2 = findViewById(R.id.btn_new2);

        btnActivities.setOnClickListener(v ->{
            Intent intent =new Intent(HomeActivity.this,ActivitiesActivity.class);
            startActivity(intent);

                showToast((String) btnActivities.getContentDescription());
        });




        btnEvents.setOnClickListener(v ->
                showToast((String) btnEvents.getContentDescription()));

        btnNew1.setOnClickListener(v ->
                showToast((String) btnNew1.getContentDescription()));

        btnNew2.setOnClickListener(v ->
                showToast((String) btnNew2.getContentDescription()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    private void applyFontSizeIfNeeded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean increaseFont = prefs.getBoolean("increase_font_size", false);

        if (increaseFont) {
            float newSize = 25f;  // o el tamaño que quieras

            TextView tvTitle1 = findViewById(R.id.utiles);
            TextView tvTitle2 = findViewById(R.id.productos);
            TextView tvTitle3 = findViewById(R.id.cuota);
            TextView tvTitle4 = findViewById(R.id.anuncio);
            tvTitle1.setTextSize(newSize);
            tvTitle2.setTextSize(newSize);
            tvTitle3.setTextSize(newSize);
            tvTitle4.setTextSize(newSize);
            // etc., todos los que quieras modificar
        }
    }


}