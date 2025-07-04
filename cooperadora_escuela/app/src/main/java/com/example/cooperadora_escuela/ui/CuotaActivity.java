package com.example.cooperadora_escuela.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.AboutUsActivity;
import com.example.cooperadora_escuela.HomeActivity;
import com.example.cooperadora_escuela.ProductsActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.WebActivity;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuotaActivity extends AppCompatActivity {

    private static final String TAG = "CuotaActivity";

    private Spinner spinnerTipo, spinnerMes, spinnerAnio;
    private Button btnCrearCuota, btnDescargarPdf;
    private ImageView imageViewQR;

    private TextView txtMonto;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private final List<String> tipos = Arrays.asList("mensual", "anual");
    private final List<String> meses = Arrays.asList(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuota);

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
                startActivity(new Intent(CuotaActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                Intent intent = new Intent(CuotaActivity.this, ProductsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(CuotaActivity.this, ProfileActivity.class));
//            } else if (id == R.id.nav_accesibilidad) {
//                Intent intent = new Intent(ContactActivity.this, AccessibilityActivity.class);
//                startActivity(intent);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
            } else if (id == R.id.nav_contact) {
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(CuotaActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                Intent intent = new Intent(CuotaActivity.this, WebActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                logoutUser(); // llamamos a salir
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerMes = findViewById(R.id.spinnerMes);
        spinnerAnio = findViewById(R.id.spinnerAnio);
        btnCrearCuota = findViewById(R.id.btnCrearCuota);
        btnDescargarPdf = findViewById(R.id.btnDescargarPdf);
        imageViewQR = findViewById(R.id.imageViewQR);



        imageViewQR.setVisibility(View.GONE);
        btnDescargarPdf.setVisibility(View.GONE);

        verificarPermisos();

        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        ArrayAdapter<String> mesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meses);
        mesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(mesAdapter);

        List<String> listaAnios = new ArrayList<>();
        for (int anio = 2025; anio <= 2030; anio++) {
            listaAnios.add(String.valueOf(anio));
        }
        ArrayAdapter<String> anioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaAnios);
        anioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnio.setAdapter(anioAdapter);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean mostrarMes = tipos.get(i).equals("mensual");
                spinnerMes.setVisibility(mostrarMes ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btnCrearCuota.setOnClickListener(view -> crearCuota());
        Button btnEnviarComprobante = findViewById(R.id.btnEnviarComprobante);
        btnEnviarComprobante.setOnClickListener(view -> {
            Intent intent = new Intent(CuotaActivity.this, SendCheckActivity .class);
            startActivity(intent);
        });
    }

    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        }
    }

    private void crearCuota() {
        String tipo = spinnerTipo.getSelectedItem().toString().toLowerCase();
        String anioStr = spinnerAnio.getSelectedItem().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("tipo", tipo);
            json.put("anio", Integer.parseInt(anioStr));

            if (tipo.equals("mensual")) {
                int mes = spinnerMes.getSelectedItemPosition() + 1;
                json.put("mes", mes);
            }

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

            UserService userService = ApiUser.getRetrofit(this).create(UserService.class);
            Call<ResponseBody> call = userService.crearCuota(body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonString = response.body().string();
                            JSONObject jsonResponse = new JSONObject(jsonString);

                            Toast.makeText(CuotaActivity.this, "Cuota creada con éxito", Toast.LENGTH_SHORT).show();

                            String qrBase64 = jsonResponse.optString("qr_pago_base64");
                            int cuotaId = jsonResponse.getInt("id");

                            if (qrBase64 != null && !qrBase64.isEmpty()) {
                                mostrarQrDesdeBase64(qrBase64);
                            } else {
                                obtenerQrDesdeBackend(cuotaId);
                            }

                            btnDescargarPdf.setVisibility(View.VISIBLE);
                            btnDescargarPdf.setOnClickListener(v -> descargarPdf(cuotaId));
                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando la respuesta JSON", e);
                            Toast.makeText(CuotaActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CuotaActivity.this, "Error al crear la cuota", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CuotaActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error armando JSON", e);
        }
    }

    private void obtenerQrDesdeBackend(int cuotaId) {
        UserService userService = ApiUser.getRetrofit(this).create(UserService.class);
        Call<ResponseBody> call = userService.obtenerQrPago(cuotaId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageViewQR.setImageBitmap(bitmap);
                        imageViewQR.setVisibility(View.VISIBLE);
                        Toast.makeText(CuotaActivity.this, "QR cargado correctamente", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error mostrando QR desde imagen", e);
                        Toast.makeText(CuotaActivity.this, "Error al mostrar el QR", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CuotaActivity.this, "No se pudo obtener el QR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CuotaActivity.this, "Error de red al obtener QR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarQrDesdeBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return;

        try {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageViewQR.setImageBitmap(decodedByte);
            imageViewQR.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar el QR base64", e);
        }
    }

    private void descargarPdf(int cuotaId) {
        UserService userService = ApiUser.getRetrofit(this).create(UserService.class);

        Call<ResponseBody> call = userService.descargarComprobante(cuotaId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean ok = guardarPdfEnDispositivo(response.body(), cuotaId);
                    Toast.makeText(CuotaActivity.this, ok ? "PDF descargado" : "Error al guardar PDF", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CuotaActivity.this, "No se pudo descargar el comprobante", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CuotaActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean guardarPdfEnDispositivo(ResponseBody body, int cuotaId) {
        try {
            File carpetaDescargas = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File archivo = new File(carpetaDescargas, "comprobante_cuota_" + cuotaId + ".pdf");

            InputStream inputStream = body.byteStream();
            OutputStream outputStream = new FileOutputStream(archivo);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", archivo);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error guardando PDF", e);
            return false;
        }
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
