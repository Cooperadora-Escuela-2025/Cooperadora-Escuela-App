package com.example.cooperadora_escuela;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputMessage;
    private Button btnSend, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Referencias
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputMessage = findViewById(R.id.input_message);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarFormulario();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void enviarFormulario() {
        String nombre = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String mensaje = inputMessage.getText().toString().trim();

        // Validar nombre: no vacío, mínimo 3 caracteres, solo letras y espacios
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

        // Validar email: no vacío y formato válido
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

        // Validar mensaje: no vacío, mínimo 10 caracteres y caracteres válidos básicos
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
        // Solo permitimos letras, números, espacios y signos básicos de puntuación
        if (!mensaje.matches("[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ,.¡!¿?\\-\\n\\r]+")) {
            inputMessage.setError("El mensaje contiene caracteres no permitidos");
            inputMessage.requestFocus();
            return;
        }

        // Simulación de envío
        Toast.makeText(this, "Mensaje enviado correctamente. ¡Gracias por contactarnos!", Toast.LENGTH_LONG).show();

        // Limpiar formulario
        inputName.setText("");
        inputEmail.setText("");
        inputMessage.setText("");
    }

}
