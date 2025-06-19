package com.example.cooperadora_escuela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.cooperadora_escuela.adapter.CartAdapter;
import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.ui.AccessibilityActivity;
import com.example.cooperadora_escuela.ui.LoginActivity;
import com.example.cooperadora_escuela.ui.ProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements Cart.CartListener, CartAdapter.OnItemClickListener {

    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private Cart cart;
    private DrawerLayout drawerLayout;

    private HashMap<Product, Integer> initialCartState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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

            if (id == R.id.nav_home) {
                startActivity(new Intent(CartActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_product) {
                startActivity(new Intent(CartActivity.this, ProductsActivity.class));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
//            } else if (id == R.id.nav_accesibilidad) {
//                Intent intent = new Intent(CartActivity.this, AccessibilityActivity.class);
//                startActivity(intent);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
            } else if (id == R.id.nav_contact) {
                startActivity(new Intent(CartActivity.this, ContactActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(CartActivity.this, AboutUsActivity.class));
            } else if (id == R.id.nav_web) {
                startActivity(new Intent(CartActivity.this, WebActivity.class));
            } else if (id == R.id.nav_logout) {
                logoutUser();
                return true;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        cart = Cart.getInstance();
        cart.setListener(this);

        // Guardar estado original del carrito
        initialCartState = new HashMap<>(cart.getProductMap());

        setupRecyclerView();
        setupButtons();

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        updateTotalPrice();
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

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

    private void setupRecyclerView() {
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(new ArrayList<>(cart.getProductMap().keySet()));
        cartAdapter.setOnItemClickListener(this);
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void setupButtons() {
        MaterialButton checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            if (cart.getProductMap().isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_cart_message), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });

        MaterialButton backToProductsButton = findViewById(R.id.backToProductsButton);
        backToProductsButton.setOnClickListener(v -> {
            restoreAllStocks();
            sendCartBack();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartData();
    }

    private void refreshCartData() {
        cartAdapter.updateProducts(new ArrayList<>(cart.getProductMap().keySet()));
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = cart.getTotalPrice();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        String totalFormatted = currencyFormat.format(total);

        totalPriceTextView.setText(getString(R.string.total_label, totalFormatted));
        totalPriceTextView.setContentDescription(getString(R.string.total_content_description, totalFormatted));
    }

    @Override
    public void onProductAdded(Product product) {
        cartAdapter.updateProducts(new ArrayList<>(cart.getProductMap().keySet()));
        updateTotalPrice();
    }

    @Override
    public void onProductRemoved(Product product) {
        cartAdapter.updateProducts(new ArrayList<>(cart.getProductMap().keySet()));
        updateTotalPrice();
    }

    @Override
    public void onCartCleared() {
        cartAdapter.updateProducts(new ArrayList<>());
        updateTotalPrice();
    }

    @Override
    public void onOutOfStock(Product product) {
        Toast.makeText(this, product.getName() + " sin stock disponible", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveClick(Product product) {
        // Remover una unidad del producto del carrito
        cart.removeProduct(product);

        // Obtener la cantidad que queda en el carrito después de remover
        int quantityInCartAfter = cart.getProductQuantity(product);

        Toast.makeText(this, product.getName() + " eliminado del carrito. Quedan " + quantityInCartAfter + " unidades en el carrito", Toast.LENGTH_SHORT).show();

        // Devolver 1 unidad al stock
        product.setQuantity(product.getQuantity() + 1);

        // Refrescar la UI del carrito
        refreshCartData();
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            restoreAllStocks();
            sendCartBack();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cart.setListener(null);
        cartAdapter.setOnItemClickListener(null);
    }

    private void restoreAllStocks() {
        for (Product product : initialCartState.keySet()) {
            int initialQuantity = initialCartState.get(product);
            int currentQuantity = cart.getProductMap().getOrDefault(product, 0);
            int difference = initialQuantity - currentQuantity;
            product.setQuantity(product.getQuantity() + difference);
        }
    }

    private void sendCartBack() {
        Intent intent = new Intent();
        ArrayList<Product> updatedProducts = new ArrayList<>(cart.getProductMap().keySet());
        intent.putParcelableArrayListExtra("updatedProducts", updatedProducts);
        setResult(RESULT_OK, intent);
    }
}

