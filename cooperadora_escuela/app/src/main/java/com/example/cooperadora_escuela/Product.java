package com.example.cooperadora_escuela;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class Product implements Parcelable {
    private final int id;
    private final String name;
    private final double price;
    private final int imageResource;

    public Product(int id, @NonNull String name, double price, int imageResource) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
    }

    private Product(@NonNull Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.price = in.readDouble();
        this.imageResource = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<>() {
        @NonNull
        @Override
        public Product createFromParcel(@NonNull Parcel in) {
            return new Product(in);
        }

        @NonNull
        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(imageResource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                Double.compare(product.price, price) == 0 &&
                imageResource == product.imageResource &&
                name.equals(product.name);
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageResource=" + imageResource +
                '}';
    }
}