package com.example.cooperadora_escuela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ContactActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputMessage;
    private Button btnSend;

    // Para menú lateral
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

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
                startActivity(new Intent(ContactActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                Intent intent = new Intent(ContactActivity.this, ProductsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_cuota) {
                Toast.makeText(ContactActivity.this, "Cuota", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(ContactActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_accesibilidad) {
                Intent intent = new Intent(ContactActivity.this, AccessibilityActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_contact) {
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(ContactActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                Intent intent = new Intent(ContactActivity.this, WebActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                logoutUser(); // llamamos a salir
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Referencias para el formulario (sin btnBack porque el drawer ya tiene botón)
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputMessage = findViewById(R.id.input_message);
        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(v -> enviarFormulario());
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

    private void enviarFormulario() {
        String nombre = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String mensaje = inputMessage.getText().toString().trim();

        // Validaciones (igual que antes)
        if (TextUtils.isEmpty(nombre)) {
            inputName.setError("Por favor ingresa tu nombre");
            inputName.requestFocus();
            return;
        }
        if (nombre.length() < 3) {
            inputName.setError("El nombre debe tener al menos 3 caracteres");
            inputName.requestFocus();
            return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            inputName.setError("El nombre solo puede contener letras y espacios");
            inputName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Por favor ingresa tu email");
            inputEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Por favor ingresa un email válido");
            inputEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mensaje)) {
            inputMessage.setError("Por favor ingresa un mensaje");
            inputMessage.requestFocus();
            return;
        }
        if (mensaje.length() < 10) {
            inputMessage.setError("El mensaje debe tener al menos 10 caracteres");
            inputMessage.requestFocus();
            return;
        }
        if (!mensaje.matches("[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ,.¡!¿?\\-\\n\\r]+")) {
            inputMessage.setError("El mensaje contiene caracteres no permitidos");
            inputMessage.requestFocus();
            return;
        }

        Toast.makeText(this, "Mensaje enviado correctamente. ¡Gracias por contactarnos!", Toast.LENGTH_LONG).show();

        inputName.setText("");
        inputEmail.setText("");
        inputMessage.setText("");
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
