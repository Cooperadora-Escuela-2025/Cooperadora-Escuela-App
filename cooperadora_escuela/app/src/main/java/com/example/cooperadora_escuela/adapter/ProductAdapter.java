package com.example.cooperadora_escuela.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductoViewHolder> {
    private Context context;
    private List<Product> productos;
    private OnItemClickListener listener;

    // Interfaz para manejar clicks externos (editar/agregar)
    public interface OnItemClickListener {
        void onEditClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productos, OnItemClickListener listener) {
        this.context = context;
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Product producto = productos.get(position);

        Log.d("DEBUG_STOCK", "Producto: " + producto.getName() + " - Stock: " + producto.getQuantity());


        // Formatear precio en ARS
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        holder.txtNombre.setText(producto.getName());
        holder.txtPrecio.setText(currencyFormat.format(producto.getPrice()));
        holder.txtStock.setText("Stock: " + producto.getQuantity());


        // Manejar imagen
        String baseUrl = "http://10.0.2.2:8000";
        String imagePath = producto.getImage();
        String fullImageUrl = imagePath;

        if (imagePath != null && !imagePath.isEmpty()) {
            if (!imagePath.startsWith("http")) {
                fullImageUrl = baseUrl + imagePath;
            }
            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // imagen por defecto mientras carga
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_background);
        }

        // Setear listeners para botones Editar y Agregar
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(producto);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            if (producto.getQuantity() > 0) {
                if (listener != null) listener.onAddToCartClick(producto);
                // Descontamos stock visualmente
                producto.setQuantity(producto.getQuantity() - 1);
                notifyItemChanged(position); // Se actualiza solo el item afectado
            } else {
                Toast.makeText(context, "No hay stock disponible para " + producto.getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio, txtStock;
        ImageView imgProducto;
        Button btnEdit, btnAddToCart;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.productName);
            txtPrecio = itemView.findViewById(R.id.productPrice);
            imgProducto = itemView.findViewById(R.id.productImage);
            txtStock = itemView.findViewById(R.id.productStock);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            btnAddToCart = itemView.findViewById(R.id.addToCart);
        }
    }
}
