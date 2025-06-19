package com.example.cooperadora_escuela.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.AboutUsActivity;
import com.example.cooperadora_escuela.ContactActivity;
import com.example.cooperadora_escuela.HomeActivity;
import com.example.cooperadora_escuela.ProductsActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.WebActivity;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendCheckActivity extends AppCompatActivity {

    private static final String TAG = "EnviarComprobante";

    private TextView tvAlumnoNombre, tvAlumnoApellido, tvAlumnoDni,nombreArchivo;
    private EditText etTutorNombre, etTutorApellido, etTutorDni, etParentesco;
    private Button btnSeleccionarArchivo, btnEnviar;

    private Uri archivoUri = null;

    private String alumnoNombre, alumnoApellido, alumnoDni;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private final ActivityResultLauncher<Intent> seleccionarArchivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoUri = result.getData().getData();
                    if (archivoUri != null) {
                        String nombre = obtenerNombreArchivo(archivoUri);
                        nombreArchivo.setText("Archivo seleccionado: " + nombre);
                        Toast.makeText(this, "Archivo seleccionado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        nombreArchivo.setText("Error al seleccionar archivo");
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_check);

        tvAlumnoNombre = findViewById(R.id.tvAlumnoNombre);
        tvAlumnoApellido = findViewById(R.id.tvAlumnoApellido);
        tvAlumnoDni = findViewById(R.id.tvAlumnoDni);

        etTutorNombre = findViewById(R.id.etTutorNombre);
        etTutorApellido = findViewById(R.id.etTutorApellido);
        etTutorDni = findViewById(R.id.etTutorDni);
        etParentesco = findViewById(R.id.etParentesco);

        nombreArchivo = findViewById(R.id.nombreArchivo);

        btnSeleccionarArchivo = findViewById(R.id.btnSeleccionarArchivo);
        btnEnviar = findViewById(R.id.btnEnviar);


        // cargar datos del alumno desde SharedPreferences
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences prefs = EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            alumnoNombre = prefs.getString("first_name", "Sin nombre");
            alumnoApellido = prefs.getString("last_name", "Sin apellido");
            alumnoDni = prefs.getString("dni", "Sin DNI");

            tvAlumnoNombre.setText(alumnoNombre);
            tvAlumnoApellido.setText(alumnoApellido);
            tvAlumnoDni.setText(alumnoDni);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer datos del alumno", Toast.LENGTH_SHORT).show();
        }

        btnSeleccionarArchivo.setOnClickListener(v -> abrirSelectorArchivo());

        btnEnviar.setOnClickListener(v -> enviarComprobante());

        verificarPermisos();
        Log.d("SendCheckActivity", "Alumno Nombre: " + alumnoNombre);
        Log.d("SendCheckActivity", "Alumno Apellido: " + alumnoApellido);
        Log.d("SendCheckActivity", "Alumno DNI: " + alumnoDni);

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

            // Aca se agregan navegación a las activities
            if (id == R.id.nav_home) {
                startActivity(new Intent(SendCheckActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                Intent intent = new Intent(SendCheckActivity.this, ProductsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(SendCheckActivity.this, ProfileActivity.class));
//            } else if (id == R.id.nav_accesibilidad) {
//                Intent intent = new Intent(ContactActivity.this, AccessibilityActivity.class);
//                startActivity(intent);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
            } else if (id == R.id.nav_contact) {
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(SendCheckActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                Intent intent = new Intent(SendCheckActivity.this, WebActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                logoutUser(); // llamamos a salir
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void abrirSelectorArchivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "application/pdf",
                "image/jpeg",
                "image/png",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Selecciona el comprobante"));


    }



    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    private void enviarComprobante() {
        if (etTutorNombre.getText().toString().trim().isEmpty() ||
                etTutorApellido.getText().toString().trim().isEmpty() ||
                etTutorDni.getText().toString().trim().isEmpty() ||
                etParentesco.getText().toString().trim().isEmpty() ||
                archivoUri == null) {
            Toast.makeText(this, "Complete todos los datos del tutor y seleccione el archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(archivoUri);
            if (inputStream == null) {
                Toast.makeText(this, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] bytes = readBytesFromInputStream(inputStream);
            String mimeType = getContentResolver().getType(archivoUri);
            if (mimeType == null || mimeType.trim().isEmpty()) {
                mimeType = "application/octet-stream";
            }

            String fileName = obtenerNombreArchivo(archivoUri);
            if (fileName == null) fileName = "comprobante";

            RequestBody requestFile = RequestBody.create(bytes, MediaType.parse(mimeType));
            MultipartBody.Part archivoPart = MultipartBody.Part.createFormData("archivo", fileName, requestFile);

            RequestBody alumnoNombreRB = RequestBody.create(alumnoNombre, MediaType.parse("text/plain"));
            RequestBody alumnoApellidoRB = RequestBody.create(alumnoApellido, MediaType.parse("text/plain"));
            RequestBody alumnoDniRB = RequestBody.create(alumnoDni, MediaType.parse("text/plain"));
            RequestBody tutorNombreRB = RequestBody.create(etTutorNombre.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody tutorApellidoRB = RequestBody.create(etTutorApellido.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody tutorDniRB = RequestBody.create(etTutorDni.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody parentescoRB = RequestBody.create(etParentesco.getText().toString().trim(), MediaType.parse("text/plain"));

            UserService userService = ApiUser.getRetrofit(this).create(UserService.class);
            Call<ResponseBody> call = userService.enviarComprobante(
                    archivoPart,
                    alumnoNombreRB,
                    alumnoApellidoRB,
                    alumnoDniRB,
                    tutorNombreRB,
                    tutorApellidoRB,
                    tutorDniRB,
                    parentescoRB
            );
            Log.d(TAG, "Alumno Nombre: " + alumnoNombre);
            Log.d(TAG, "Alumno Apellido: " + alumnoApellido);
            Log.d(TAG, "Alumno DNI: " + alumnoDni);
            Log.d(TAG, "Tutor Nombre: " + etTutorNombre.getText().toString().trim());
            Log.d(TAG, "Tutor Apellido: " + etTutorApellido.getText().toString().trim());
            Log.d(TAG, "Tutor DNI: " + etTutorDni.getText().toString().trim());
            Log.d(TAG, "Parentesco: " + etParentesco.getText().toString().trim());
            Log.d(TAG, "Archivo URI: " + (archivoUri != null ? archivoUri.toString() : "null"));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SendCheckActivity.this, "Comprobante enviado correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(SendCheckActivity.this, "Error al enviar comprobante", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error response: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SendCheckActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error retrofit", t);
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error leyendo archivo", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error leyendo archivo", e);
        }
    }

    private byte[] readBytesFromInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputStream.close();
        return byteBuffer.toByteArray();
    }

    private String obtenerNombreArchivo(Uri uri) {
        String nombre = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        nombre = cursor.getString(index);
                    }
                }
            }
        }
        if (nombre == null) {
            nombre = uri.getLastPathSegment();
        }
        return nombre;
    }

    //cerrar sesion
    private void logoutUser(){
        try{
            MasterKey masterKey=new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences= EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // borra todos los tokens
            editor.apply();

            // volver al login eliminando el historial
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Cerraste sesión con exito", Toast.LENGTH_SHORT).show();
        }catch(GeneralSecurityException | IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
        }
    }
}
