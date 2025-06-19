package com.example.cooperadora_escuela.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderApi {
    @POST("orders/")
    Call<Void> createOrder(@Body OrderRequest orderRequest);
}
