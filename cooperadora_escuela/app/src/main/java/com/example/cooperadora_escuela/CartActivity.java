package com.example.cooperadora_escuela;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cooperadora_escuela.models.Product;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements Cart.CartListener, CartAdapter.OnItemClickListener {

    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = Cart.getInstance();
        cart.setListener(this);

        setupRecyclerView();
        setupButtons();

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        updateTotalPrice();
    }

    private void setupRecyclerView() {
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart.getProducts());
        cartAdapter.setOnItemClickListener(this);
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void setupButtons() {
        MaterialButton checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            if (cart.getProducts().isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_cart_message), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });

        MaterialButton backToProductsButton = findViewById(R.id.backToProductsButton);
        backToProductsButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartData();
    }

    private void refreshCartData() {
        cartAdapter.updateProducts(cart.getProducts());
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
    public void onProductAdded(Product product, int position) {
        cartAdapter.addProduct(product);
        updateTotalPrice();
    }

    @Override
    public void onProductRemoved(Product product, int position) {
        cartAdapter.removeProduct(product);
        updateTotalPrice();
    }

    @Override
    public void onCartCleared() {
        cartAdapter.updateProducts(new ArrayList<>());
        updateTotalPrice();
    }

    @Override
    public void onRemoveClick(Product product) {
        cart.removeProduct(product);
        Toast.makeText(this, product.getName() + " eliminado del carrito", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cart.setListener(null);
        cartAdapter.setOnItemClickListener(null);
    }
}
