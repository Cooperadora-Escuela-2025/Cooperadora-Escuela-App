package com.example.cooperadora_escuela.network.auth;

import com.example.cooperadora_escuela.models.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApi {

    @GET("api/products/")
    Call<List<Product>> getAllProducts();

    @POST("api/products/")
    Call<Product> createProduct(@Body Product product);

    // Ahora actualizamos por ID
    @PUT("api/products/{id}/")
    Call<Product> updateProductById(@Path("id") int id, @Body Product product);

    // Ahora eliminamos por ID
    @DELETE("api/products/{id}/")
    Call<Void> deleteProductById(@Path("id") int id);
}
