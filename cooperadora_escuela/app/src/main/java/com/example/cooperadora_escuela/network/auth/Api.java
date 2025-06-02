package com.example.cooperadora_escuela.network.auth;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//retrofit, coneccion al backend de django
public class Api {

    //si utilizan el emulador de android utilizar esta opcion y comentar la otra url
    private static final String BASE_URL = "http://127.0.0.1:8000/";

    //si utilizan el celular usar esta url y comentar la otra url su Dirección IPv4
//    private static final String BASE_URL = "http://192tu direccion ipv4....:8000/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}