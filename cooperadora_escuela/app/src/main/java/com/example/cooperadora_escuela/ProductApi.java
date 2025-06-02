package com.example.cooperadora_escuela;


import com.example.cooperadora_escuela.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductApi {

    // Obtener todos los productos
    @GET("api/products/")
    Call<List<Product>> getAllProducts();

    // Crear un nuevo producto
    @POST("api/products/")
    Call<Product> createProduct(@Body Product product);

    // Actualizar producto por nombre
    @PUT("api/products/{name}/")
    Call<Product> updateProduct(@Path("name") String name, @Body Product product);

    // Eliminar producto por nombre
    @DELETE("api/products/{name}/")
    Call<Void> deleteProduct(@Path("name") String name);
}
