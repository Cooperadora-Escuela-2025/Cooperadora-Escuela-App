package com.example.cooperadora_escuela.network.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//retrofit, coneccion al backend de django
public class ApiUser {

    //si utilizan el emulador de android utilizar esta opcion y comentar la otra url
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    //si utilizan el celular usar esta url y comentar la otra url su Dirección IPv4
//    private static final String BASE_URL = "http://192tu direccion ipv4....:8000/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {

            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    String token = null;
                    try {
                        MasterKey masterKey = new MasterKey.Builder(context)
                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                .build();

                        SharedPreferences encryptedPrefs = EncryptedSharedPreferences.create(
                                context,
                                "MyPrefs", // coincide con el nombre en LoginActivity
                                masterKey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
                        token = encryptedPrefs.getString("access_token", null);
                    } catch (GeneralSecurityException | IOException e) {
                        e.printStackTrace();
                    }
                    Request originalRequest = chain.request();
                    String path = originalRequest.url().encodedPath();

                    // no enviar token en login ni register
                    if (path.equals("/login/") || path.equals("/register/")) {
                        return chain.proceed(originalRequest);
                    }

                    Request.Builder builder = originalRequest.newBuilder();

                    if (token != null) {
                        builder.header("Authorization", "Bearer " + token);
                    }

                    return chain.proceed(builder.build());
                }
            };


            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;


    }
}
