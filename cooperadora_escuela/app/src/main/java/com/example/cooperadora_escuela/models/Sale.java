package com.example.cooperadora_escuela.models;

import java.io.Serializable;

public class Sale implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private String dni;
    private double total;
    private String paymentMethod;
    private String date;

    public Sale(int id, String firstName, String lastName, String dni, double total, String paymentMethod, String date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.date = date;
    }

    public Sale(String firstName, String lastName, String dni, double total, String paymentMethod, String date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.date = date;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDni() {
        return dni;
    }

    public double getTotal() {
        return total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDate() {
        return date;
    }
}
