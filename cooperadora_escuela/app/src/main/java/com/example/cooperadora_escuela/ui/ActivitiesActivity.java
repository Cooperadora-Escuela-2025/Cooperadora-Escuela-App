package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.AboutUsActivity;
import com.example.cooperadora_escuela.ContactActivity;
import com.example.cooperadora_escuela.HomeActivity;
import com.example.cooperadora_escuela.ProductsActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.WebActivity;
import com.example.cooperadora_escuela.models.Activities;
import com.example.cooperadora_escuela.adapter.ActivitiesAdapter;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

    private RecyclerView recyclerActividades;
    private ActivitiesAdapter adapter;
    private List<Activities> listaActividades;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        recyclerActividades = findViewById(R.id.recyclerActividades);
        recyclerActividades.setLayoutManager(new LinearLayoutManager(this));

        listaActividades = new ArrayList<>();
        listaActividades.add(new Activities("Feria de Ciencias", "Presentación de proyectos científicos"));
        listaActividades.add(new Activities("Día del Estudiante", "Juegos y actividades recreativas para todos"));
        listaActividades.add(new Activities("Obra de Teatro", "Función teatral a cargo de alumnos de 5to año"));
        listaActividades.add(new Activities("Torneo de Fútbol", "Competencia deportiva entre cursos"));
        listaActividades.add(new Activities("Excursión", "Salida educativa a un museo local"));

        adapter = new ActivitiesAdapter(listaActividades);
        recyclerActividades.setAdapter(adapter);

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
                    startActivity(new Intent(ActivitiesActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_product) {
                    Intent intent = new Intent(ActivitiesActivity.this, ProductsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(ActivitiesActivity.this, ProfileActivity.class));
//                } else if (id == R.id.nav_accesibilidad) {
//                    Intent intent = new Intent(ActivitiesActivity.this, AccessibilityActivity.class);
//                    startActivity(intent);
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                    return true;
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(ActivitiesActivity.this, ContactActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(ActivitiesActivity.this, AboutUsActivity.class));
                } else if (id == R.id.nav_web) {
                    Intent intent = new Intent(ActivitiesActivity.this, WebActivity.class);
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
