package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.HomeActivity;
import com.example.cooperadora_escuela.MainActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import com.example.cooperadora_escuela.network.auth.LoginRequest;
import com.example.cooperadora_escuela.network.auth.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin;
    UserService authService;
    TextView idMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        // inicializar vistas
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        idMessage=findViewById(R.id.idMessage);

        String emailFromRegister = getIntent().getStringExtra("email");
        if (emailFromRegister != null && !emailFromRegister.isEmpty()) {
            editEmail.setText(emailFromRegister);
        }

        // inicializar Retrofit
        authService = ApiUser.getRetrofit(LoginActivity.this).create(UserService.class);



        // acción del botón
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        TextView textViewRegister = findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            //Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            idMessage.setText("Completa todos los campos!");

            return;
        }
        if (!isValidEmail(email)) {
            idMessage.setText("Ingresa un correo válido.");
            return;
        }

        if (password.length() < 6) {
            idMessage.setText("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = authService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String accessToken = loginResponse.getAccess();
                    String refreshToken = loginResponse.getRefresh();

                    Log.d("LOGIN", "Access: " + accessToken);
                    Log.d("LOGIN", "Usuario: " + loginResponse.getUser().getEmail());
                    try {
                        // crear clave maestra
                        MasterKey masterKey = new MasterKey.Builder(LoginActivity.this)
                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                .build();

                        // crear preferencias cifradas
                        SharedPreferences encryptedPrefs = EncryptedSharedPreferences.create(
                                LoginActivity.this,
                                "MyPrefs",
                                masterKey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );

                        // guardar los tokens de forma segura
                        SharedPreferences.Editor editor = encryptedPrefs.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();


                        // ir a la pantalla de dashboard
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (GeneralSecurityException | IOException e) {
                        e.printStackTrace();
                        Log.e("LOGIN", "Error al guardar los tokens: " + response.code());
                        //Toast.makeText(LoginActivity.this, "Error al guardar los tokens", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    idMessage.setText("Email o contraseña incorrectos!");
                    Log.e("LOGIN", "Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN", "Error de red: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
