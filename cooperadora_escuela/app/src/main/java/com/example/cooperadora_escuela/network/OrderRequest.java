package com.example.cooperadora_escuela.network;

import java.util.List;

public class OrderRequest {
    private String name;
    private String surname;
    private String dni;
    private double total;
    private String payment_method;
    private List<OrderItem> products;

    public OrderRequest(String name, String surname, String dni, double total, String payment_method, List<OrderItem> products) {
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.total = total;
        this.payment_method = payment_method;
        this.products = products;
    }

    public String getName() { return name; }

    public String getDni() { return dni; }
    public double getTotal() { return total; }

}
