package com.example.cooperadora_escuela;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cooperadora_escuela.ui.DashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnIrADashboard = findViewById(R.id.btnDashboard);

        btnIrADashboard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }
}

        // Verificación de permisos (simplificada)
//        if (!PermissionManager.hasStoragePermission()) {
//            PermissionManager.checkAndRequestPermissions();
//        }

//        Button btnLogin = findViewById(R.id.btnLogin);
//        EditText etUsername = findViewById(R.id.etUsername);
//        EditText etPassword = findViewById(R.id.etPassword);
//
//        btnLogin.setOnClickListener(v -> {
//            String username = etUsername.getText().toString();
//            String password = etPassword.getText().toString();

//            if (username.equals("admin") && password.equals("admin")) {
//                if (PermissionManager.hasStoragePermission()) {
//                    startProductsActivity();
//                } else {
//                    showPermissionRequiredDialog();
//                }
//            } else {
//                Toast.makeText(MainActivity.this,
//                        "Usuario o contraseña incorrectos",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void startProductsActivity() {
//        startActivity(new Intent(this, ProductsActivity.class));
//        finish();
//    }

//    private void showPermissionRequiredDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Permiso requerido")
//                .setMessage("La aplicación necesita permisos de almacenamiento para funcionar correctamente")
//                .setPositiveButton("Solicitar permisos", (dialog, which) ->
//                        PermissionManager.checkAndRequestPermissions())
//                .setNegativeButton("Cancelar", null)
//                .show();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!PermissionManager.hasStoragePermission()) {
//            PermissionManager.checkAndRequestPermissions();
//        }
//    }
//}