package com.example.cooperadora_escuela.models;

public class Produ {
    private int id;
    private String name;
    private double price;
    private String image; // nombre del campo que devolvés en JSON

    // Getters y setters (puedes generarlos con el IDE)
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImage() { return image; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImage_url(String image_url) { this.image= image_url; }
}
