package com.example.cooperadora_escuela;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cooperadora_escuela.models.Product;

import java.text.NumberFormat;
import java.util.*;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Product> productList;
    private final Map<Product, Integer> productQuantities;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onRemoveClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CartAdapter(@NonNull List<Product> products) {
        this.productList = new ArrayList<>();
        this.productQuantities = new HashMap<>();
        updateProducts(products);
    }

    public void updateProducts(@NonNull List<Product> newProducts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProductDiffCallback(productList, newProducts));

        productList.clear();
        productList.addAll(newProducts);

        productQuantities.clear();
        for (Product product : newProducts) {
            int quantity = Cart.getInstance().getProductQuantity(product);
            productQuantities.put(product, quantity);
        }

        diffResult.dispatchUpdatesTo(this);
    }

    public void addProduct(@NonNull Product product) {
        int position = productList.indexOf(product);

        productQuantities.put(product, Cart.getInstance().getProductQuantity(product));

        if (position == -1) {
            productList.add(product);
            notifyItemInserted(productList.size() - 1);
        } else {
            notifyItemChanged(position, productQuantities.get(product));
        }
    }

    public void removeProduct(@NonNull Product product) {
        int position = productList.indexOf(product);

        if (position != -1) {
            int quantity = Cart.getInstance().getProductQuantity(product);

            if (quantity == 0) {
                productList.remove(position);
                productQuantities.remove(product);
                notifyItemRemoved(position);
            } else {
                productQuantities.put(product, quantity);
                notifyItemChanged(position, quantity);
            }
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) return;

        int quantity = productQuantities.getOrDefault(product, 1);
        holder.bind(product, quantity);

        holder.removeButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onRemoveClick(product);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Integer quantity = (Integer) payloads.get(0);
            holder.updateQuantity(quantity);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productQuantity;
        private final ImageButton removeButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            removeButton = itemView.findViewById(R.id.removeButton);
            removeButton.setContentDescription(itemView.getContext().getString(R.string.remove_item));
        }

        void bind(@NonNull Product product, int quantity) {
            productName.setText(product.getName());
            productName.setContentDescription(product.getName());

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
            String price = currencyFormat.format(product.getPrice());
            productPrice.setText(price);
            productPrice.setContentDescription(
                    itemView.getContext().getString(R.string.price_content_description, price));

            Glide.with(itemView.getContext())
                    .load(product.getImage())
                    .into(productImage);

            productImage.setContentDescription(itemView.getContext().getString(R.string.product_image_description));

            updateQuantity(quantity);
        }

        void updateQuantity(int quantity) {
            String quantityText = itemView.getContext().getString(R.string.quantity_label, quantity);
            productQuantity.setText(quantityText);
            productQuantity.setContentDescription(quantityText);
        }
    }

    private static class ProductDiffCallback extends DiffUtil.Callback {
        private final List<Product> oldList;
        private final List<Product> newList;

        ProductDiffCallback(List<Product> oldList, List<Product> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}

