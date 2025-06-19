package com.example.cooperadora_escuela.network.auth;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit, conexión al backend de Django
public class Api {

    // Si usan el emulador de Android usar esta opción y comentar la otra url
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    // Si usan el celu usar esta url y comentar la otra url. Su Dirección IPv4
    // private static final String BASE_URL = "http://192.tu.direccion.ipv4:8000/";

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