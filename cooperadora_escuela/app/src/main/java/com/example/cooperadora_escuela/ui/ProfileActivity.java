package com.example.cooperadora_escuela.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import com.example.cooperadora_escuela.network.profile.ProfileRequest;
import com.example.cooperadora_escuela.network.profile.ProfileResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etDni, etTelefono;
    private AutoCompleteTextView etTurno, etGrado;
    private TextView tvEmail;
    private Button btnGuardar;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        applyThemeFromPrefs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




        // Toolbar con flecha atrás
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Obtener token seguro
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences encryptedPrefs = EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            token = "Bearer " + encryptedPrefs.getString("access_token", "");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al acceder a datos seguros", Toast.LENGTH_SHORT).show();
            token = "";
        }

        // Referencias a views
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDni = findViewById(R.id.etDni);
        etTurno = findViewById(R.id.etTurno);
        etGrado = findViewById(R.id.etGrado);
        etTelefono = findViewById(R.id.etTelefono);
        tvEmail = findViewById(R.id.tvEmail);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Adaptadores para AutoCompleteTextView
        String[] turnos = {"Mañana", "Tarde"};
        ArrayAdapter<String> adapterTurno = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, turnos);
        etTurno.setAdapter(adapterTurno);

        String[] grados = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapterGrado = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, grados);
        etGrado.setAdapter(adapterGrado);

        // Cargar perfil desde API
        cargarPerfil();

        // Guardar perfil al hacer click
        btnGuardar.setOnClickListener(v -> actualizarPerfil());

        //llamar a los dropdown
        etTurno.setOnClickListener(v -> etTurno.showDropDown());
        etGrado.setOnClickListener(v -> etGrado.showDropDown());

        applyFontSizeIfNeeded();

        //aumenta fuente
        SwitchCompat switchFontSize = findViewById(R.id.switch_font_size); 
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        switchFontSize.setChecked(prefs.getBoolean("increase_font_size", false));

        switchFontSize.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("increase_font_size", isChecked).apply();
            recreate(); // para que tome el cambio de tamaño
        });

    }

    // Manejar flecha atrás en Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Carga el perfil del usuario y llena los campos
    private void cargarPerfil() {
        if (token.isEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        UserService userService = ApiUser.getRetrofit(ProfileActivity.this).create(UserService.class);
        Call<ProfileResponse> call = userService.getProfile(token);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();

                    etNombre.setText(profile.getFirst_name());
                    etApellido.setText(profile.getLast_name());
                    etDni.setText(profile.getDni());
                    tvEmail.setText(profile.getEmail());

                    // Poner texto sin activar filtro para AutoCompleteTextView
                    String turno = profile.getShift();
                    if ("Mañana".equalsIgnoreCase(turno) || "Tarde".equalsIgnoreCase(turno)) {
                        etTurno.setText(turno, false);
                    } else {
                        etTurno.setText("", false);
                    }

                    String grado = profile.getGrade_year();
                    if (grado != null && grado.matches("[1-5]")) {
                        etGrado.setText(grado, false);
                    } else {
                        etGrado.setText("", false);
                    }

                    etTelefono.setText(profile.getTelephone());

                } else {
                    Toast.makeText(ProfileActivity.this, "Error al obtener perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Actualiza el perfil enviando datos al backend
    private void actualizarPerfil() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String turno = etTurno.getText().toString().trim();
        String grado = etGrado.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!turno.equalsIgnoreCase("Mañana") && !turno.equalsIgnoreCase("Tarde")) {
            //Toast.makeText(this, "Turno debe ser 'Mañana' o 'Tarde'", Toast.LENGTH_SHORT).show();
            etDni.setError("Turno debe ser 'Mañana' o 'Tarde");
            return;
        }

        if (!grado.matches("[1-5]")) {
            //Toast.makeText(this, "Grado debe ser un número del 1 al 5", Toast.LENGTH_SHORT).show();
            etDni.setError("Grado debe ser un número del 1 al 5");
            return;
        }

        // Validar teléfono: solo dígitos, 10 u 11 caracteres
        if (!telefono.matches("^\\d{10,11}$")) {
            etDni.setError("El teléfono debe tener 10 u 11 dígitos numéricos");
            return;
        }

        // valida dni entre 7 y 8 digitos
        if (!dni.matches("^\\d{7,8}$")) {
            etDni.setError("El DNI debe tener entre 7 y 8 dígitos numéricos");
            etDni.requestFocus();
            return;
        }

        try {
            int dniNum = Integer.parseInt(dni);
            if (dniNum < 1000000 || dniNum > 99999999) {
                etDni.setError("El DNI debe estar entre 1.000.000 y 99.999.999");
                etDni.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etDni.setError("El DNI debe contener solo números");
            etDni.requestFocus();
            return;
        }
        ProfileRequest request = new ProfileRequest(nombre, apellido, email, dni, turno, grado, telefono);
        UserService userService = ApiUser.getRetrofit(ProfileActivity.this).create(UserService.class);

        Call<ProfileResponse> call = userService.updateProfile(token, request);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    cargarPerfil();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de red al actualizar perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }


private void applyFontSizeIfNeeded() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean increaseFont = prefs.getBoolean("increase_font_size", false);

    if (increaseFont) {
        float newSize = 25f;

        // aumentar tamaño solo si el usuario lo quiere
        etNombre.setTextSize(newSize);
        etApellido.setTextSize(newSize);
        etDni.setTextSize(newSize);
        etTurno.setTextSize(newSize);
        etGrado.setTextSize(newSize);
        etTelefono.setTextSize(newSize);
        tvEmail.setTextSize(newSize);
        btnGuardar.setTextSize(newSize);
    }
}


}