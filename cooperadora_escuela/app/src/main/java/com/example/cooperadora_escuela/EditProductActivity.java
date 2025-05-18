package com.example.cooperadora_escuela;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProductActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        dbHelper = new DatabaseHelper(this);
        product = getIntent().getParcelableExtra("product");

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        EditText etName = findViewById(R.id.etProductName);
        EditText etPrice = findViewById(R.id.etProductPrice);
        Button btnDelete = findViewById(R.id.btnDeleteProduct);

        if (product != null) {
            etName.setText(product.getName());
            etPrice.setText(String.valueOf(product.getPrice()));
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnSaveProduct).setOnClickListener(v -> saveProduct());
        findViewById(R.id.btnDeleteProduct).setOnClickListener(v -> deleteProduct());
    }

    private void saveProduct() {
        EditText etName = findViewById(R.id.etProductName);
        EditText etPrice = findViewById(R.id.etProductPrice);

        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            showError("Complete todos los campos");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);

            if (product == null) {
                createProduct(name, price);
            } else {
                updateProduct(name, price);
            }
        } catch (NumberFormatException e) {
            showError("Ingrese un precio válido");
        }
    }

    private void createProduct(String name, double price) {
        int newId = dbHelper.getAllProducts().size() + 1;
        Product newProduct = new Product(newId, name, price, R.drawable.libreta_calificaciones);

        long result = dbHelper.addProduct(newProduct);
        if(result != -1) {
            showSuccess("Producto creado exitosamente");
        } else {
            showError("Error al crear el producto");
        }
    }

    private void updateProduct(String name, double price) {
        product = new Product(product.getId(), name, price, product.getImageResource());

        int rowsAffected = dbHelper.updateProduct(product);
        if(rowsAffected > 0) {
            showSuccess("Producto actualizado exitosamente");
        } else {
            showError("Error al actualizar el producto");
        }
    }

    private void deleteProduct() {
        if (product != null) {
            int rowsDeleted = dbHelper.deleteProduct(product.getId());
            if(rowsDeleted > 0) {
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