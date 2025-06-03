package com.example.cooperadora_escuela;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cooperadora_escuela.ui.DashboardActivity;
import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

public class AboutUsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Referencias al menú lateral
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btn_back);

        // Toolbar como ActionBar
        setSupportActionBar(toolbar);

        // Toggle del menú hamburguesa
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

                if (id == R.id.nav_home) {
                    Toast.makeText(AboutUsActivity.this, "Inicio", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_product) {
                    Intent intent = new Intent(AboutUsActivity.this, ProductsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_cuota) {
                    Toast.makeText(AboutUsActivity.this, "Cuota", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_perfil) {
                    Intent intent = new Intent(AboutUsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(AboutUsActivity.this, ContactActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(AboutUsActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Botón para volver al Dashboard
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}