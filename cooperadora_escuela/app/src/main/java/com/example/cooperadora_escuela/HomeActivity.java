package com.example.cooperadora_escuela;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configurar Toolbar
        setupToolbar();

        // Configurar botones
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupButtons() {
        // Configurar listeners para los botones
        Button btnActivities = findViewById(R.id.btn_activities);
        Button btnEvents = findViewById(R.id.btn_events);
        Button btnNew1 = findViewById(R.id.btn_new1);
        Button btnNew2 = findViewById(R.id.btn_new2);

        btnActivities.setOnClickListener(v ->
                showToast((String) btnActivities.getContentDescription()));

        btnEvents.setOnClickListener(v ->
                showToast((String) btnEvents.getContentDescription()));

        btnNew1.setOnClickListener(v ->
                showToast((String) btnNew1.getContentDescription()));

        btnNew2.setOnClickListener(v ->
                showToast((String) btnNew2.getContentDescription()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}