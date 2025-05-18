package com.example.cooperadora_escuela;

public final class Sale {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String dni;
    private final double total;
    private final String paymentMethod;
    private final String date;

    public Sale(int id, String firstName, String lastName, String dni,
                double total, String paymentMethod, String date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.date = date;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDni() { return dni; }
    public double getTotal() { return total; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDate() { return date; }
}