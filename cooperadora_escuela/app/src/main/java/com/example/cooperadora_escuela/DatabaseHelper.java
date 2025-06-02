package com.example.cooperadora_escuela;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import com.example.cooperadora_escuela.network.auth.Api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "cooperadora.db";
    private static final int DATABASE_VERSION = 2;

    private final ProductApi productApi;

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_NAME = "name"; // Será PRIMARY KEY
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        productApi = Api.getClient().create(ProductApi.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COLUMN_PRODUCT_NAME + " TEXT PRIMARY KEY,"
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL,"
                + COLUMN_PRODUCT_IMAGE + " TEXT NOT NULL)";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImage());

        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();

        productApi.createProduct(product).enqueue(new Callback<Product>() {
            @Override public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Producto sincronizado con API: " + product.getName());
                } else {
                    Log.e(TAG, "Error al sincronizar producto con API, code: " + response.code());
                }
            }
            @Override public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "Error sincronizando producto con API", t);
            }
        });

        return result;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImage());

        int rows = db.update(TABLE_PRODUCTS, values,
                COLUMN_PRODUCT_NAME + " = ?",
                new String[]{product.getName()});
        db.close();

        productApi.updateProduct(product.getName(), product).enqueue(new Callback<Product>() {
            @Override public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Producto actualizado en API: " + product.getName());
                } else {
                    Log.e(TAG, "Error al actualizar producto en API, code: " + response.code());
                }
            }
            @Override public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "Error al actualizar producto en API", t);
            }
        });

        return rows;
    }

    public int deleteProduct(String productName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS,
                COLUMN_PRODUCT_NAME + " = ?",
                new String[]{productName});
        db.close();

        productApi.deleteProduct(productName).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Producto eliminado en API: " + productName);
                } else {
                    Log.e(TAG, "Error: API no eliminó producto, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error al eliminar producto en API", t);
            }
        });

        return rows;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)));
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return productList;
    }
}
