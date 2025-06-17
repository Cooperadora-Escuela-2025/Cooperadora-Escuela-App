package com.example.cooperadora_escuela.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.example.cooperadora_escuela.models.Sale;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.Locale;


public class PurchaseReceiptActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private Sale sale;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipt);

        // Setup Toolbar y Drawer
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

        Button exportButton = findViewById(R.id.exportPdfButton);
        exportButton.setOnClickListener(v -> checkPermissionAndSavePdf());

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(PurchaseReceiptActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_product) {
                    startActivity(new Intent(PurchaseReceiptActivity.this, ProductsActivity.class));
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(PurchaseReceiptActivity.this, ProfileActivity.class));

                } else if (id == R.id.nav_contact) {
                    startActivity(new Intent(PurchaseReceiptActivity.this, ContactActivity.class));
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(PurchaseReceiptActivity.this, AboutUsActivity.class));
                } else if (id == R.id.nav_web) {
                    startActivity(new Intent(PurchaseReceiptActivity.this, WebActivity.class));
                } else if (id == R.id.nav_logout) {
                    logoutUser();
                    return true;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Obtener datos de la venta
        sale = (Sale) getIntent().getSerializableExtra("sale");

        if (sale != null) {
            ((TextView) findViewById(R.id.receiptName)).setText(sale.getFirstName() + " " + sale.getLastName());
            ((TextView) findViewById(R.id.receiptDni)).setText(sale.getDni());
            ((TextView) findViewById(R.id.receiptPayment)).setText(sale.getPaymentMethod());
            ((TextView) findViewById(R.id.receiptTotal)).setText(
                    NumberFormat.getCurrencyInstance(new Locale("es", "AR")).format(sale.getTotal())
            );
            ((TextView) findViewById(R.id.receiptDate)).setText(sale.getDate());
        }


        // Botón para volver al home
        Button backButton = findViewById(R.id.btnBackToHome);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseReceiptActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void checkPermissionAndSavePdf() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Android 9 o anterior, pedir permiso WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_PERMISSION);
            } else {
                generatePdf();
            }
        } else {
            // Android 10 o superior no requiere permiso para MediaStore
            generatePdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePdf();
            } else {
                Toast.makeText(this, "Permiso denegado para guardar PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generatePdf() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 500, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 10, y = 25;

        paint.setTextSize(14f);
        canvas.drawText("COMPROBANTE DE COMPRA", x, y, paint);
        y += 25;

        paint.setTextSize(12f);
        canvas.drawText("Nombre: " + sale.getFirstName() + " " + sale.getLastName(), x, y += 20, paint);
        canvas.drawText("DNI: " + sale.getDni(), x, y += 20, paint);
        canvas.drawText("Método de Pago: " + sale.getPaymentMethod(), x, y += 20, paint);
        canvas.drawText("Total: " + NumberFormat.getCurrencyInstance(new Locale("es", "AR")).format(sale.getTotal()), x, y += 20, paint);
        canvas.drawText("Fecha: " + sale.getDate(), x, y += 20, paint);

        pdfDocument.finishPage(page);

        savePdfToDownloads(pdfDocument, this);

        pdfDocument.close();
    }

    private void savePdfToDownloads(PdfDocument pdfDocument, Context context) {
        String fileName = "comprobante_" + System.currentTimeMillis() + ".pdf";

        try {
            OutputStream outputStream;
            Uri uri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri == null) {
                    Toast.makeText(this, "No se pudo crear el archivo PDF", Toast.LENGTH_LONG).show();
                    return;
                }
                outputStream = context.getContentResolver().openOutputStream(uri);
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                outputStream = new FileOutputStream(file);
                uri = Uri.fromFile(file);
            }

            if (outputStream != null) {
                pdfDocument.writeTo(outputStream);
                outputStream.close();

                // Forzar que aparezca en exploradores de archivos
                MediaScannerConnection.scanFile(this, new String[]{uri.getPath()}, null, null);

                Toast.makeText(this, "Comprobante guardado en Descargas", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No se pudo abrir el flujo para guardar PDF", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void logoutUser() {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "MyPrefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            sharedPreferences.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Cerraste sesión con éxito", Toast.LENGTH_SHORT).show();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
        }
    }
}
