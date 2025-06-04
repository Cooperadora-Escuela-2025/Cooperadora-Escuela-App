package com.example.cooperadora_escuela.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Product implements Parcelable {

    private int id;
    private String name;
    private double price;
    private String image;

    // Constructor con id
    public Product(int id, String name, double price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    // Constructor sin id
    public Product(String name, double price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    // Constructor para Parcelable
    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
        image = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    // Getters y setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeString(image);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.cooperadora_escuela.models.Product product = (com.example.cooperadora_escuela.models.Product) o;
        return id == product.id &&
                Double.compare(product.price, price) == 0 &&
                image == product.image &&
                name.equals(product.name);
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageResource=" + image +
                '}';
    }
}
