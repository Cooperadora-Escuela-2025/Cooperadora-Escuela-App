package com.example.cooperadora_escuela.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.cooperadora_escuela.Cart;
import com.example.cooperadora_escuela.CartActivity;
import com.example.cooperadora_escuela.EditProductActivity;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.network.auth.Api;
import com.example.cooperadora_escuela.network.auth.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {
    private List<Product> productList;
    private Cart cart;
    private ActivityResultLauncher<Intent> editProductLauncher;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        cart = Cart.getInstance();
        productList = new ArrayList<>();

        apiService = Api.getRetrofit().create(ApiService.class);

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
        Call<List<Product>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    setupUI();
                } else {
                    Toast.makeText(ProductActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUI() {
        setupProductViews();
        setupAddToCartButtons();
    }

    private void setupProductViews() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

        for (int i = 0; i < productList.size() && i < 4; i++) {
            Product product = productList.get(i);

            int[] viewIds = getViewIdsForProduct(i);
            ImageView imageView = findViewById(viewIds[0]);
            TextView nameView = findViewById(viewIds[1]);
            TextView priceView = findViewById(viewIds[2]);
            Button editButton = findViewById(viewIds[3]);
            Button addToCartButton = findViewById(viewIds[4]);

            if (imageView != null) {
                // Aquí puedes usar Glide o Picasso para cargar imagen remota si el Product tiene URL
                imageView.setImageResource(product.getImage());
                imageView.setContentDescription(getString(R.string.product_image_description));
            }

            if (nameView != null) {
                nameView.setText(product.getName());
                nameView.setContentDescription(product.getName());
            }

            if (priceView != null) {
                priceView.setText(currencyFormat.format(product.getPrice()));
                priceView.setContentDescription(getString(R.string.price_content_description,
                        currencyFormat.format(product.getPrice())));
            }

            if (editButton != null) {
                editButton.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
                editButton.setOnClickListener(v -> launchEditProductActivity(product));
                editButton.setContentDescription(getString(R.string.edit_product_desc) + " " + product.getName());
            }

            if (addToCartButton != null) {
                addToCartButton.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
            }
        }
    }

    private int[] getViewIdsForProduct(int index) {
        int[] imageViewIds = {R.id.productImage1, R.id.productImage2, R.id.productImage3, R.id.productImage4};
        int[] nameViewIds = {R.id.productName1, R.id.productName2, R.id.productName3, R.id.productName4};
        int[] priceViewIds = {R.id.productPrice1, R.id.productPrice2, R.id.productPrice3, R.id.productPrice4};
        int[] addToCartButtonIds = {R.id.addToCart1, R.id.addToCart2, R.id.addToCart3, R.id.addToCart4};

        return new int[]{
                imageViewIds[index],
                nameViewIds[index],
                priceViewIds[index],
                addToCartButtonIds[index]
        };
    }

    private void launchEditProductActivity(Product product) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("product", (CharSequence) product);
        editProductLauncher.launch(intent);
    }

    private void setupAddToCartButtons() {
        int[] buttonIds = {R.id.addToCart1, R.id.addToCart2, R.id.addToCart3, R.id.addToCart4};

        for (int i = 0; i < buttonIds.length && i < productList.size(); i++) {
            int productIndex = i;
            Button button = findViewById(buttonIds[i]);
            if (button != null) {
                button.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
                button.setOnClickListener(v -> addProductToCart(productIndex));
                button.setContentDescription(getString(R.string.add_to_cart) + " " + productList.get(productIndex).getName());
            }
        }
    }

    private void addProductToCart(int productIndex) {
        if (productIndex < productList.size()) {
            Product product = productList.get(productIndex);
            cart.addProduct(product);
            Toast.makeText(this, product.getName() + " agregado al carrito", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewCartButton() {
        Button viewCartButton = findViewById(R.id.viewCartButton);
        if (viewCartButton != null) {
            viewCartButton.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
            viewCartButton.setOnClickListener(v -> {
                if (cart.getProducts().isEmpty()) {
                    Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, CartActivity.class));
                }
            });
        }
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
}
