package com.example.cooperadora_escuela;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cooperadora_escuela.network.OrderItem;
import com.example.cooperadora_escuela.network.OrderRequest;
import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.models.Sale;
import com.example.cooperadora_escuela.network.auth.Api;
import com.example.cooperadora_escuela.network.OrderApi;
import com.example.cooperadora_escuela.ui.PurchaseReceiptActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        dbHelper = new DatabaseHelper(this);

        double total = Cart.getInstance().getTotalPrice();
        TextView checkoutTotalTextView = findViewById(R.id.checkoutTotalTextView);
        checkoutTotalTextView.setText(getString(R.string.total_price,
                NumberFormat.getCurrencyInstance(new Locale("es", "AR")).format(total)));

        setupButtons();
        setupAccessibility();
    }

    private void setupButtons() {
        MaterialButton completePurchaseButton = findViewById(R.id.completePurchaseButton);
        completePurchaseButton.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
        completePurchaseButton.setOnClickListener(v -> completePurchase());

        MaterialButton backToCartButton = findViewById(R.id.backToCartButton);
        backToCartButton.setMinHeight(getResources().getDimensionPixelSize(R.dimen.button_min_height));
        backToCartButton.setOnClickListener(v -> finish());
    }

    private void setupAccessibility() {
        TextInputEditText firstNameEditText = findViewById(R.id.firstNameEditText);
        TextInputEditText lastNameEditText = findViewById(R.id.lastNameEditText);
        TextInputEditText dniEditText = findViewById(R.id.dniEditText);

        firstNameEditText.setContentDescription(getString(R.string.hint_first_name));
        lastNameEditText.setContentDescription(getString(R.string.hint_last_name));
        dniEditText.setContentDescription(getString(R.string.hint_dni));
    }

    private void completePurchase() {
        TextInputEditText firstNameEditText = findViewById(R.id.firstNameEditText);
        TextInputEditText lastNameEditText = findViewById(R.id.lastNameEditText);
        TextInputEditText dniEditText = findViewById(R.id.dniEditText);
        RadioGroup paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);

        String firstName = firstNameEditText.getText() != null ? firstNameEditText.getText().toString().trim() : "";
        String lastName = lastNameEditText.getText() != null ? lastNameEditText.getText().toString().trim() : "";
        String dni = dniEditText.getText() != null ? dniEditText.getText().toString().trim() : "";

        if (firstName.isEmpty() || lastName.isEmpty() || dni.isEmpty()) {
            showToast("Complete todos los campos");
            return;
        }

        if (!dni.matches("\\d{8}")) {
            showToast("DNI debe tener 8 dígitos");
            return;
        }

        int selectedId = paymentMethodRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            showToast("Seleccione método de pago");
            return;
        }

        String paymentMethod = getPaymentMethod(selectedId);
        double total = Cart.getInstance().getTotalPrice();

        Map<Product, Integer> cartProducts = Cart.getInstance().getProducts();

        if (cartProducts.isEmpty()) {
            showToast("El carrito está vacío");
            return;
        }

        // Crea los items de la orden para la API
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
            orderItems.add(new OrderItem(entry.getKey().getName(), entry.getValue()));
        }

        // Crea la orden completa
        OrderRequest orderRequest = new OrderRequest(
                firstName,
                lastName,
                dni,
                paymentMethod,
                orderItems
        );

        // Llama a la API
        OrderApi orderApi = Api.getRetrofit().create(OrderApi.class);
        orderApi.createOrder(orderRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Compra registrada exitosamente en el servidor");

                    // Registrar venta localmente
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    Sale sale = new Sale(0, firstName, lastName, dni, total, paymentMethod, currentDate);
                    long saleId = dbHelper.addSale(sale);

                    if (saleId != -1) {
                        // Vaciar carrito
                        Cart.getInstance().clearCart();

                        // Redirige al comprobante
                        Intent intent = new Intent(CheckoutActivity.this, PurchaseReceiptActivity.class);
                        intent.putExtra("sale", (Serializable) sale);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("Error al registrar la compra localmente");
                    }
                } else {
                    showToast("Error en la compra: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Error de conexión: " + t.getMessage());
            }
        });
    }

    private String getPaymentMethod(int selectedId) {
        if (selectedId == R.id.cashRadioButton) {
            return "Efectivo";
        } else if (selectedId == R.id.mercadoPagoRadioButton) {
            return "Mercado Pago";
        }
        return "Desconocido";
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
