package com.example.cooperadora_escuela.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.cooperadora_escuela.ProductsActivity;
import com.example.cooperadora_escuela.R;
import com.google.android.material.navigation.NavigationView;


public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button btnIr = findViewById(R.id.btn);
        btnIr.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, ProductsActivity.class);
            startActivity(intent);
        });







        //para el menu lateral
        // referencias
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // configurar el toolbar como actionBar
        setSupportActionBar(toolbar);

        // crear y sincroniza el toggle de la hamburguesa
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // items del menú lateral de menu_drawer
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                //se llaman los items que se quieran agregar
                if (id == R.id.nav_home) {
                    Toast.makeText(DashboardActivity.this, "Inicio", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_product) {
                    Toast.makeText(DashboardActivity.this, "Productos", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_cuota) {
                    Toast.makeText(DashboardActivity.this, "Cuota", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_perfil) {
                    Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //fin menu lateral

    }
}
