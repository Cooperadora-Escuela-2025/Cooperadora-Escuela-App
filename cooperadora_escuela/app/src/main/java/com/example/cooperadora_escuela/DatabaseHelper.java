package com.example.cooperadora_escuela;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.models.Sale;
import com.example.cooperadora_escuela.network.auth.Api;
import com.example.cooperadora_escuela.network.auth.ProductApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "cooperadora.db";
    private static final int DATABASE_VERSION = 3;

    // TABLA PRODUCTOS
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_IMAGE = "image";
    private static final String COLUMN_PRODUCT_QUANTITY = "quantity";

    // TABLA VENTAS
    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_ID = "id";
    private static final String COLUMN_SALE_FIRST_NAME = "first_name";
    private static final String COLUMN_SALE_LAST_NAME = "last_name";
    private static final String COLUMN_SALE_DNI = "dni";
    private static final String COLUMN_SALE_TOTAL = "total";
    private static final String COLUMN_SALE_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_SALE_DATE = "date";

    private final ProductApi productApi;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        productApi = Api.getRetrofit().create(ProductApi.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + COLUMN_PRODUCT_IMAGE + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0)";

        String CREATE_SALES_TABLE = "CREATE TABLE " + TABLE_SALES + " ("
                + COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SALE_FIRST_NAME + " TEXT NOT NULL, "
                + COLUMN_SALE_LAST_NAME + " TEXT NOT NULL, "
                + COLUMN_SALE_DNI + " TEXT NOT NULL, "
                + COLUMN_SALE_TOTAL + " REAL NOT NULL, "
                + COLUMN_SALE_PAYMENT_METHOD + " TEXT NOT NULL, "
                + COLUMN_SALE_DATE + " TEXT NOT NULL)";

        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_SALES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        onCreate(db);
    }

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImage());
        values.put(COLUMN_PRODUCT_QUANTITY, product.getQuantity());

        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();

        productApi.createProduct(product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Producto sincronizado con API: " + product.getName());
                } else {
                    Log.e(TAG, "Error al sincronizar producto con API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "Error al sincronizar producto con API", t);
            }
        });

        return result;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImage());
        values.put(COLUMN_PRODUCT_QUANTITY, product.getQuantity());

        int rows = db.update(TABLE_PRODUCTS, values,
                COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
        db.close();

        productApi.updateProductById(product.getId(), product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Producto actualizado en API");
                } else {
                    Log.e(TAG, "Error al actualizar producto en API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "Error al actualizar producto en API", t);
            }
        });

        return rows;
    }

    public int deleteProductById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        ProductApi productApi = Api.getRetrofit().create(ProductApi.class);
        productApi.deleteProductById(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Producto eliminado en API");
                } else {
                    Log.e("API", "Error al eliminar producto en API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Fallo al eliminar producto en API", t);
            }
        });

        return rows;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY));

                productList.add(new Product(id, name, price, image, quantity));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return productList;
    }

    public long addSale(Sale sale) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SALE_FIRST_NAME, sale.getFirstName());
        values.put(COLUMN_SALE_LAST_NAME, sale.getLastName());
        values.put(COLUMN_SALE_DNI, sale.getDni());
        values.put(COLUMN_SALE_TOTAL, sale.getTotal());
        values.put(COLUMN_SALE_PAYMENT_METHOD, sale.getPaymentMethod());
        values.put(COLUMN_SALE_DATE, sale.getDate());

        long id = db.insert(TABLE_SALES, null, values);
        db.close();
        return id;
    }
}
