package com.example.cooperadora_escuela.network.auth;

import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.network.OrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("products/")
    Call<List<Product>> getProducts();

    @POST("orders/")
    Call<Void> createOrder(@Body OrderRequest orderRequest);

}
