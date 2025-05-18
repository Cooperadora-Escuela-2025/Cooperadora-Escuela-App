package com.example.cooperadora_escuela;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private final List<Product> products;
    private CartListener listener;

    private Cart() {
        products = new ArrayList<>();
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
        products.add(product);
        if (listener != null) {
            listener.onProductAdded(product, products.size() - 1);
        }
    }

    public void removeProduct(Product product) {
        if (product == null) return;
        int index = products.indexOf(product);
        if (index != -1) {
            products.remove(index);
            if (listener != null) {
                listener.onProductRemoved(product, index);
            }
        }
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public double getTotalPrice() {
        double total = 0;
        for (Product product : products) {
            if (product != null) {
                total += product.getPrice();
            }
        }
        return total;
    }

    public void clearCart() {
        products.clear();
        if (listener != null) {
            listener.onCartCleared();
        }
    }

    public interface CartListener {
        void onProductAdded(Product product, int position);
        void onProductRemoved(Product product, int position);
        void onCartCleared();
    }
}