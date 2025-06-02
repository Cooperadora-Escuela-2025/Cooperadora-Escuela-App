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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<Product> productList;
    private final Map<String, Integer> productQuantities;
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
        productQuantities.clear();

        Map<String, Product> uniqueProducts = new HashMap<>();
        for (Product product : newProducts) {
            if (product == null) continue;

            String key = product.getName();
            productQuantities.merge(key, 1, Integer::sum);

            if (!uniqueProducts.containsKey(key)) {
                uniqueProducts.put(key, product);
            }
        }

        productList.addAll(uniqueProducts.values());
        diffResult.dispatchUpdatesTo(this);
    }

    public void addProduct(@NonNull Product product) {
        String key = product.getName();
        int position = findProductPosition(key);

        int newCount = productQuantities.merge(key, 1, Integer::sum);

        if (position == -1) {
            productList.add(product);
            notifyItemInserted(productList.size() - 1);
        } else {
            notifyItemChanged(position, newCount);
        }
    }

    public void removeProduct(@NonNull Product product) {
        String key = product.getName();
        int position = findProductPosition(key);

        if (position != -1) {
            Integer newCount = productQuantities.compute(key, (k, count) ->
                    (count == null || count <= 1) ? null : count - 1);

            if (newCount == null) {
                productList.remove(position);
                notifyItemRemoved(position);
            } else {
                notifyItemChanged(position, newCount);
            }
        }
    }

    private int findProductPosition(String key) {
        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            if (p != null && p.getName().equals(key)) {
                return i;
            }
        }
        return -1;
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
        if (product != null) {
            String key = product.getName();
            Integer quantity = productQuantities.get(key);
            holder.bind(product, quantity != null ? quantity : 1);

            holder.removeButton.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onRemoveClick(product);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Product product = productList.get(position);
            if (product != null) {
                Integer quantity = (Integer) payloads.get(0);
                holder.updateQuantity(quantity);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productQuantity;
        private final ImageButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            removeButton = itemView.findViewById(R.id.removeButton);

            removeButton.setContentDescription(itemView.getContext().getString(R.string.remove_item));
        }

        public void bind(@NonNull Product product, int quantity) {
            productName.setText(product.getName());
            productName.setContentDescription(product.getName());

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
            String price = currencyFormat.format(product.getPrice());
            productPrice.setText(price);
            productPrice.setContentDescription(
                    itemView.getContext().getString(R.string.price_content_description, price));

            // Usar Glide para cargar imagen desde URL
            Glide.with(itemView.getContext())
                    .load(product.getImage())
                    .into(productImage);

            productImage.setContentDescription(
                    itemView.getContext().getString(R.string.product_image_description));

            updateQuantity(quantity);
        }

        public void updateQuantity(int quantity) {
            String quantityText = itemView.getContext()
                    .getString(R.string.quantity_label, quantity);
            productQuantity.setText(quantityText);
            productQuantity.setContentDescription(quantityText);
        }
    }

    private static class ProductDiffCallback extends DiffUtil.Callback {
        private final List<Product> oldList;
        private final List<Product> newList;

        public ProductDiffCallback(List<Product> oldList, List<Product> newList) {
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
            return oldList.get(oldItemPosition).getName()
                    .equals(newList.get(newItemPosition).getName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
