package com.example.cooperadora_escuela.network;

public class OrderItem {
    private int product; // ID del producto
    private double unit_price;
    private int quantity;

    public OrderItem(int product, double unit_price, int quantity) {
        this.product = product;
        this.unit_price = unit_price;
        this.quantity = quantity;
    }

    public int getProduct() {
        return product;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public int getQuantity() {
        return quantity;
    }
}
