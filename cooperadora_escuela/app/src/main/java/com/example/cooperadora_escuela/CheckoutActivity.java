package com.example.cooperadora_escuela;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cooperadora_escuela.models.Sale;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        // Configurar accesibilidad para los campos de entrada
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

        String firstName = firstNameEditText.getText() != null ?
                firstNameEditText.getText().toString().trim() : "";
        String lastName = lastNameEditText.getText() != null ?
                lastNameEditText.getText().toString().trim() : "";
        String dni = dniEditText.getText() != null ?
                dniEditText.getText().toString().trim() : "";

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

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Sale sale = new Sale(0, firstName, lastName, dni, total, paymentMethod, currentDate);

        long saleId = dbHelper.addSale(sale);
        if (saleId != -1) {
            showToast("Compra registrada exitosamente");
            Cart.getInstance().clearCart();
            finish();
        } else {
            showToast("Error al registrar la compra");
        }
    }

    private String getPaymentMethod(int selectedId) {
        if (selectedId == R.id.cashRadioButton) {
            return "Efectivo";
        } else if (selectedId == R.id.debitCardRadioButton) {
            return "Tarjeta Débito";
        } else if (selectedId == R.id.creditCardRadioButton) {
            return "Tarjeta Crédito";
        } else if (selectedId == R.id.mercadoPagoRadioButton) {
            return "Mercado Pago";
        }
        return "Desconocido";
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}