package com.example.cooperadora_escuela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.ui.AccessibilityActivity;
import com.example.cooperadora_escuela.ui.LoginActivity;
import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class WebActivity extends AppCompatActivity {

    private static final String REPO_URL = "https://github.com/Cooperadora-Escuela-2025/Cooperadora-Escuela-Web";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Opcional: manejar clics en el menú
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Aca se agregan navegación a las activities
                if (id == R.id.nav_home) {
                    startActivity(new Intent(WebActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_product) {
                    Intent intent = new Intent(WebActivity.this, ProductsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(WebActivity.this, ProfileActivity.class));
//                } else if (id == R.id.nav_accesibilidad) {
//                    Intent intent = new Intent(WebActivity.this, AccessibilityActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                    return true;
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(WebActivity.this, ContactActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(WebActivity.this, AboutUsActivity.class));
                } else if (id == R.id.nav_web) {
                    Intent intent = new Intent(WebActivity.this, WebActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                    logoutUser(); // llamamos a salir
                    return true;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Botón que abre el repositorio web
        ImageButton btnWeb = findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
            startActivity(browserIntent);
        });
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
}
