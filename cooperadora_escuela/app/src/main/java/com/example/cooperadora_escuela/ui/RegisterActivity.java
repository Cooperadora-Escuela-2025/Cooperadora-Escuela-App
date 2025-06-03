package com.example.cooperadora_escuela.ui;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import com.example.cooperadora_escuela.network.auth.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etPassword2;
    Button btnRegister;
    TextView idMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        btnRegister = findViewById(R.id.btnRegister);
        idMessage = findViewById(R.id.idMessage);


        btnRegister.setOnClickListener(v -> {

            String first_name = etFirstName.getText().toString().trim();
            String last_name = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String password2 = etPassword2.getText().toString();

            if (!password.equals(password2)) {
                idMessage.setText("Las contraseñas no coinciden");
                return;
            }

            String passwordValidationError = validatePassword(password);
            if (passwordValidationError != null) {
                idMessage.setText(passwordValidationError);
                return;
            }

            RegisterRequest request = new RegisterRequest(first_name, last_name, email, password, password2);
            registerUser(request);
        });

        TextView textViewRegister = findViewById(R.id.textRegisterLink);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }



    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres, incluir letras, números y un carácter especial.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe incluir al menos una letra mayúscula.";
        }
        if (!password.matches(".*\\d.*")) {
            return "La contraseña debe incluir al menos un número.";
        }
        if (!password.matches(".*[@$!%*?&._-].*")) {
            return "La contraseña debe incluir al menos un carácter especial (@$!%*?&._-).";
        }
        return null;
    }


    private void registerUser(RegisterRequest request) {
        UserService authService = ApiUser.getRetrofit(RegisterActivity.this).create(UserService.class);

        Call<ResponseBody> call = authService.registerUser(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso. Redirigiendo al login...", Toast.LENGTH_SHORT).show();

                    //traer el email del formulario
                    String email = request.getEmail();

                    // intent hacia Login con el email
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();

                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        idMessage.setText("Error en el registro: debe completar todos los campos!! " );
                        Log.e("REGISTRO", "Error del servidor: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        idMessage.setText("Error desconocido");
                        Log.e("REGISTRO", "Error al leer el cuerpo del error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                idMessage.setText("Error de red: " + t.getMessage());
                Log.e("REGISTRO", "Falla de red", t);
            }
        });
    }

}
