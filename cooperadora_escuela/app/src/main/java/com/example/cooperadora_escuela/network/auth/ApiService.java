package com.example.cooperadora_escuela.network.auth;

import com.example.cooperadora_escuela.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("products/")
    Call<List<Product>> getProducts();

}
