package com.example.cooperadora_escuela;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ContactActivity extends AppCompatActivity {

    EditText etNombre, etEmail, etMensaje;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String email = etEmail.getText().toString();
            String mensaje = etMensaje.getText().toString();

            // Aquí podrías hacer un envío real o simularlo
            Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show();

            /* Limpiar campos */
            etNombre.setText("");
            etEmail.setText("");
            etMensaje.setText("");
        });
    }
}
