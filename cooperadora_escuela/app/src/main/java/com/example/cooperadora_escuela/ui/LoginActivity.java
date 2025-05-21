package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cooperadora_escuela.MainActivity;
import com.example.cooperadora_escuela.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnIrALogin = findViewById(R.id.btnLogin);
        btnIrALogin.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

    }
}
