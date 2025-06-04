package com.example.cooperadora_escuela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.bumptech.glide.Glide;
import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.network.auth.Api;
import com.example.cooperadora_escuela.network.auth.ProductApi;
import com.example.cooperadora_escuela.ui.AccessibilityActivity;
import com.example.cooperadora_escuela.ui.LoginActivity;
import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {

    private List<Product> productList;
    private Cart cart;
    private ProductApi productApi;
    private LinearLayout productsContainer;
    private ActivityResultLauncher<Intent> editProductLauncher;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

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

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Aca se agregan navegación a las activities
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProductsActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                Intent intent = new Intent(ProductsActivity.this, ProductsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_cuota) {
                Toast.makeText(ProductsActivity.this, "Cuota", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(ProductsActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_accesibilidad) {
                Intent intent = new Intent(ProductsActivity.this, AccessibilityActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_contact) {
                Intent intent = new Intent(ProductsActivity.this, ContactActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(ProductsActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                Intent intent = new Intent(ProductsActivity.this, WebActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                //Toast.makeText(DashboardActivity.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                logoutUser(); // llamamos a salir
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        cart = Cart.getInstance();
        productApi = Api.getRetrofit().create(ProductApi.class);
        productsContainer = findViewById(R.id.productsContainer);

        editProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchProductsFromApi();
                        Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                    }
                });

        fetchProductsFromApi();
        setupViewCartButton();
    }

    private void fetchProductsFromApi() {
        productApi.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    displayProductsDynamically();
                } else {
                    Toast.makeText(ProductsActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Fallo al obtener productos: " + t.getMessage());
                Toast.makeText(ProductsActivity.this, "Error de red al obtener productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductsDynamically() {
        productsContainer.removeAllViews();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Product product : productList) {
            View itemView = inflater.inflate(R.layout.item_product, productsContainer, false);

            ImageView imageView = itemView.findViewById(R.id.productImage);
            TextView nameView = itemView.findViewById(R.id.productName);
            TextView priceView = itemView.findViewById(R.id.productPrice);
            Button btnEdit = itemView.findViewById(R.id.btnEditProduct);
            Button btnAddToCart = itemView.findViewById(R.id.addToCart);

            nameView.setText(product.getName());
            priceView.setText(currencyFormat.format(product.getPrice()));

            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageView);

            btnEdit.setOnClickListener(v -> launchEditProductActivity(product));

            btnAddToCart.setOnClickListener(v -> {
                cart.addProduct(product);
                Toast.makeText(this, product.getName() + " agregado al carrito", Toast.LENGTH_SHORT).show();
            });

            productsContainer.addView(itemView);
        }
    }

    private void setupViewCartButton() {
        Button viewCartButton = findViewById(R.id.viewCartButton);
        if (viewCartButton != null) {
            viewCartButton.setOnClickListener(v -> {
                if (cart.getProducts().isEmpty()) {
                    Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, CartActivity.class));
                }
            });
        }
    }

    private void launchEditProductActivity(Product product) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("product", product);
        editProductLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_product) {
            launchAddProductActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchAddProductActivity() {
        Intent intent = new Intent(this, EditProductActivity.class);
        editProductLauncher.launch(intent);
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
