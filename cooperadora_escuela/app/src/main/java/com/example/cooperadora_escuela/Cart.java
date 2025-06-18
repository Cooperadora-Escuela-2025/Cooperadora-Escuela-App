package com.example.cooperadora_escuela;

import com.example.cooperadora_escuela.models.Product;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private static Cart instance;
    private final Map<Product, Integer> productMap;
    private CartListener listener;

    private Cart() {
        productMap = new HashMap<>();
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void setListener(CartListener listener) {
        this.listener = listener;
    }

    public void addProduct(Product product) {
        if (product == null) return;

        int currentQuantity = productMap.containsKey(product) ? productMap.get(product) : 0;

        // Verificar stock antes de agregar
        if (product.getQuantity() > currentQuantity) {
            productMap.put(product, currentQuantity + 1);
            if (listener != null) {
                listener.onProductAdded(product);
            }
        } else {
            if (listener != null) {
                listener.onOutOfStock(product);
            }
        }
    }

    public void removeProduct(Product product) {
        if (product == null || !productMap.containsKey(product)) return;

        int currentQuantity = productMap.get(product);
        if (currentQuantity > 1) {
            productMap.put(product, currentQuantity - 1);
        } else {
            productMap.remove(product);
        }

        if (listener != null) {
            listener.onProductRemoved(product);
        }
    }

    public int getProductQuantity(Product product) {
        return productMap.getOrDefault(product, 0);
    }

    public Map<Product, Integer> getProducts() {
        return new HashMap<>(productMap);
    }

    public double getTotalPrice() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : productMap.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public int getTotalCount() {
        int totalCount = 0;
        for (int quantity : productMap.values()) {
            totalCount += quantity;
        }
        return totalCount;
    }

    public Map<Product, Integer> getProductMap() {
        return new HashMap<>(productMap);
    }

    public void clearCart() {
        productMap.clear();
        if (listener != null) {
            listener.onCartCleared();
        }
    }

    public interface CartListener {
        void onProductAdded(Product product);
        void onProductRemoved(Product product);
        void onCartCleared();
        void onOutOfStock(Product product);
    }
}
