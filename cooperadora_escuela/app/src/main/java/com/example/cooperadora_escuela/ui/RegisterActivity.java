package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cooperadora_escuela.MainActivity;
import com.example.cooperadora_escuela.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnIrARegister=findViewById(R.id.btnRegister);
        btnIrARegister.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }
}
