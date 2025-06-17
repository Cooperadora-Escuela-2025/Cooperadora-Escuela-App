package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.AboutUsActivity;
import com.example.cooperadora_escuela.ContactActivity;
import com.example.cooperadora_escuela.HomeActivity;
import com.example.cooperadora_escuela.ProductsActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.WebActivity;
import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.models.Purchase;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class HistoryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private List<Purchase> purchaseHistory = new ArrayList<>();
    private TextView textViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        // Configurar toolbar y drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Manejar selección del menú lateral
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Aca se agregan navegación a las activities
            if (id == R.id.nav_home) {
                startActivity(new Intent(HistoryActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                Intent intent = new Intent(HistoryActivity.this, ProductsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(HistoryActivity.this, ProfileActivity.class));
//
            } else if (id == R.id.nav_contact) {
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(HistoryActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                Intent intent = new Intent(HistoryActivity.this, WebActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                logoutUser(); // llamamos a salir
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        textViewHistory = findViewById(R.id.textViewHistory);

        UserService service = ApiUser.getRetrofit(this).create(UserService.class);

        service.getPurchaseHistory().enqueue(new Callback<List<Purchase>>() {
            @Override
            public void onResponse(Call<List<Purchase>> call, Response<List<Purchase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    purchaseHistory = response.body();
                    mostrarHistorial(purchaseHistory);
                } else {
                    textViewHistory.setText("Error al obtener historial");
                }
            }

            @Override
            public void onFailure(Call<List<Purchase>> call, Throwable t) {
                textViewHistory.setText("Error: " + t.getMessage());
            }
        });
    }

    private void mostrarHistorial(List<Purchase> history) {
        if (history == null || history.isEmpty()) {
            textViewHistory.setText("📭 Todavía no realizaste ninguna compra.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Purchase p : history) {
            sb.append("🧾 Compra #").append(p.getPurchase_id()).append("\n");
            sb.append("📅 Fecha: ").append(p.getCreated_at()).append("\n");
            sb.append("💳 Método de pago: ").append(p.getOrder().getPayment_method()).append("\n");
            sb.append("💰 Total: $").append(p.getOrder().getTotal()).append("\n");
            //sb.append("📦 Productos:\n");

//            for (Product prod : p.getOrder().getProducts()) {
//                sb.append("   - ").append(prod.getName())
//                        .append(" (x").append(prod.getQuantity()).append(") - $")
//                        .append(prod.getPrice()).append("\n");
//            }

            sb.append("\n------------------------------\n\n");
        }

        textViewHistory.setText(sb.toString());
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