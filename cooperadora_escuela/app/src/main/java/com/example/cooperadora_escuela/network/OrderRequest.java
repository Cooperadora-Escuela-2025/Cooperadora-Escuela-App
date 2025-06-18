package com.example.cooperadora_escuela.network;

import java.util.List;

public class OrderRequest {
    private String first_name;
    private String last_name;
    private String dni;
    private String payment_method;
    private List<OrderItem> products;

    public OrderRequest(String first_name, String last_name, String dni, String payment_method, List<OrderItem> products) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.dni = dni;
        this.payment_method = payment_method;
        this.products = products;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getDni() {
        return dni;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public List<OrderItem> getProducts() {
        return products;
    }
}
