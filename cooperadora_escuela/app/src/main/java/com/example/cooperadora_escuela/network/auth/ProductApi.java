package com.example.cooperadora_escuela.network.auth;

import com.example.cooperadora_escuela.models.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApi {

    @GET("products/")
    Call<List<Product>> getAllProducts();

    @POST("products/")
    Call<Product> createProduct(@Body Product product);

    // Actualizar por ID
    @PUT("products/{id}/")
    Call<Product> updateProductById(@Path("id") int id, @Body Product product);

    // Eliminar por ID
    @DELETE("products/{id}/")
    Call<Void> deleteProductById(@Path("id") int id);
}
