package com.example.cooperadora_escuela.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductoViewHolder> {
    private Context context;
    private List<Product> productos;

    public ProductAdapter(Context context, List<Product> productos) {
        this.context = context;
        this.productos = productos;
    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position) {
        Product producto = productos.get(position);
        holder.txtNombre.setText(producto.getName());
        holder.txtPrecio.setText("$" + producto.getPrice());

        String baseUrl = "http://10.0.2.2:8000";
        String imagePath = producto.getImage();
        String fullImageUrl = null;

        if (imagePath != null) {

            if (!imagePath.startsWith("http")) {
                fullImageUrl = baseUrl + imagePath;
            } else {
                fullImageUrl = imagePath;
            }

            Glide.with(context)
                    .load(fullImageUrl)
                    .into(holder.imgProducto);
        } else {
            Log.d("ProductoImage", "Imagen recibida: " + producto.getImage());

        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio;
        ImageView imgProducto;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.nombreProducto);
            txtPrecio = itemView.findViewById(R.id.precioProducto);
            imgProducto = itemView.findViewById(R.id.imagenProducto);
        }
    }
}
