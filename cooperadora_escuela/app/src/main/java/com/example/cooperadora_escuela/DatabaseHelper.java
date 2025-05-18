package com.example.cooperadora_escuela;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "cooperadora.db";
    private static final int DATABASE_VERSION = 2;

    // Tabla Productos
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_IMAGE = "image_resource";

    // Tabla Ventas
    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_ID = "id";
    private static final String COLUMN_SALE_FIRST_NAME = "first_name";
    private static final String COLUMN_SALE_LAST_NAME = "last_name";
    private static final String COLUMN_SALE_DNI = "dni";
    private static final String COLUMN_SALE_TOTAL = "total";
    private static final String COLUMN_SALE_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_SALE_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL,"
                + COLUMN_PRODUCT_IMAGE + " INTEGER)";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_SALES_TABLE = "CREATE TABLE " + TABLE_SALES + "("
                + COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SALE_FIRST_NAME + " TEXT NOT NULL,"
                + COLUMN_SALE_LAST_NAME + " TEXT NOT NULL,"
                + COLUMN_SALE_DNI + " TEXT NOT NULL,"
                + COLUMN_SALE_TOTAL + " REAL NOT NULL,"
                + COLUMN_SALE_PAYMENT_METHOD + " TEXT NOT NULL,"
                + COLUMN_SALE_DATE + " TEXT NOT NULL)";
        db.execSQL(CREATE_SALES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        onCreate(db);
    }

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, product.getId());
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImageResource());

        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();

        if(result == -1) {
            Log.e(TAG, "Error al agregar producto: " + product.getName());
        } else {
            Log.d(TAG, "Producto agregado exitosamente: " + product.getName());
        }
        return result;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE)));
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return productList;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(COLUMN_PRODUCT_IMAGE, product.getImageResource());

        int rowsAffected = db.update(TABLE_PRODUCTS, values,
                COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
        db.close();

        if(rowsAffected > 0) {
            Log.d(TAG, "Producto actualizado: " + product.getName());
        } else {
            Log.e(TAG, "Error al actualizar producto: " + product.getName());
        }
        return rowsAffected;
    }

    public int deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PRODUCTS,
                COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();

        if(rowsDeleted > 0) {
            Log.d(TAG, "Producto eliminado con ID: " + productId);
        } else {
            Log.e(TAG, "Error al eliminar producto con ID: " + productId);
        }
        return rowsDeleted;
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

        if(id == -1) {
            Log.e(TAG, "Error al agregar venta para: " + sale.getFirstName());
        } else {
            Log.d(TAG, "Venta registrada exitosamente para: " + sale.getFirstName());
        }
        return id;
    }
}