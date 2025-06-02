package com.example.cooperadora_escuela;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProductActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Product product;

    private EditText etName;
    private EditText etPrice;
    private EditText etImageUrl;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etProductPrice);
        etImageUrl = findViewById(R.id.etProductImageUrl);
        btnDelete = findViewById(R.id.btnDeleteProduct);

        // Botón Volver debajo del botón Guardar
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Obtener datos del Intent
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", -1);
        String image = getIntent().getStringExtra("product_image");

        if (name != null && price >= 0 && image != null) {
            product = new Product(name, price, image);
            fillFields(product);
            btnDelete.setVisibility(Button.VISIBLE);
        } else {
            btnDelete.setVisibility(Button.GONE);
        }

        findViewById(R.id.btnSaveProduct).setOnClickListener(v -> saveProduct());
        btnDelete.setOnClickListener(v -> deleteProduct());
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

        Product newProduct = new Product(name, price, imageUrl);

        if (product == null) {
            // Nuevo producto
            long result = dbHelper.addProduct(newProduct);
            if (result != -1) {
                showSuccess("Producto creado exitosamente");
            } else {
                showError("Error al crear el producto");
            }
        } else {
            // Actualizar producto existente
            int rows = dbHelper.updateProduct(newProduct);
            if (rows > 0) {
                showSuccess("Producto actualizado exitosamente");
            } else {
                showError("Error al actualizar el producto");
            }
        }
    }

    private void deleteProduct() {
        if (product != null) {
            int rows = dbHelper.deleteProduct(product.getName());
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
