package com.example.cooperadora_escuela;

import android.content.Intent;
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

import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

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

            if (id == R.id.nav_home) {
                Toast.makeText(ContactActivity.this, "Inicio", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_product) {
                startActivity(new Intent(ContactActivity.this, ProductsActivity.class));
            } else if (id == R.id.nav_cuota) {
                Toast.makeText(ContactActivity.this, "Cuota", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(ContactActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_about) {
                Intent intent = new Intent(ContactActivity.this, AboutUsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                Toast.makeText(ContactActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
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
