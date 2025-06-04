package com.example.cooperadora_escuela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.ui.AccessibilityActivity;
import com.example.cooperadora_escuela.ui.LoginActivity;
import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EditProductActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Product product;

    private EditText etName;
    private EditText etPrice;
    private EditText etImageUrl;
    private Button btnDelete;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Configurar Toolbar y Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Aca se agregan navegación a las activities
            if (id == R.id.nav_home) {
                startActivity(new Intent(EditProductActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                Intent intent = new Intent(EditProductActivity.this, ProductsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(EditProductActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_accesibilidad) {
                Intent intent = new Intent(EditProductActivity.this, AccessibilityActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_contact) {
                Intent intent = new Intent(EditProductActivity.this, ContactActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_about) {
                startActivity(new Intent(EditProductActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                Intent intent = new Intent(EditProductActivity.this, WebActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                logoutUser(); // llamamos a salir
                return true;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });




        // 🧱 Inicializar vista
        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etProductPrice);
        etImageUrl = findViewById(R.id.etProductImageUrl);
        btnDelete = findViewById(R.id.btnDeleteProduct);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🧾 Obtener datos del intent
        int productId = getIntent().getIntExtra("product_id", -1);
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", -1);
        String image = getIntent().getStringExtra("product_image");

        if (productId != -1 && name != null && price >= 0 && image != null) {
            product = new Product(productId, name, price, image);
            fillFields(product);
            btnDelete.setVisibility(Button.VISIBLE);
        } else {
            btnDelete.setVisibility(Button.GONE);
        }

        findViewById(R.id.btnSaveProduct).setOnClickListener(v -> saveProduct());
        btnDelete.setOnClickListener(v -> deleteProduct());
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

    private void fillFields(Product product) {
        etName.setText(product.getName());
        etPrice.setText(String.valueOf(product.getPrice()));
        etImageUrl.setText(product.getImage());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
            showError("Complete todos los campos");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                showError("Ingrese un precio válido");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Ingrese un precio válido");
            return;
        }

        if (product == null) {
            Product newProduct = new Product(name, price, imageUrl);
            long result = dbHelper.addProduct(newProduct);
            if (result != -1) {
                showSuccess("Producto creado exitosamente");
            } else {
                showError("Error al crear el producto");
            }
        } else {
            Product updatedProduct = new Product(product.getId(), name, price, imageUrl);
            int rows = dbHelper.updateProduct(updatedProduct);
            if (rows > 0) {
                showSuccess("Producto actualizado exitosamente");
            } else {
                showError("Error al actualizar el producto");
            }
        }
    }

    private void deleteProduct() {
        if (product != null) {
            int rows = dbHelper.deleteProductById(product.getId());
            if (rows > 0) {
                showSuccess("Producto eliminado exitosamente");
            } else {
                showError("Error al eliminar el producto");
            }
        }
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
