package com.example.cooperadora_escuela.network;
import com.example.cooperadora_escuela.models.Produ;
import com.example.cooperadora_escuela.network.auth.LoginRequest;
import com.example.cooperadora_escuela.network.auth.LoginResponse;
import com.example.cooperadora_escuela.network.auth.RegisterRequest;
import com.example.cooperadora_escuela.network.profile.ProfileRequest;
import com.example.cooperadora_escuela.network.profile.ProfileResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserService {
    @POST("register/")
    Call<ResponseBody> registerUser(@Body RegisterRequest request);

    @Headers("Content-Type: application/json")
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("profile/")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);

    @PUT("profile/")
    Call<ProfileResponse> updateProfile(@Header("Authorization") String token, @Body ProfileRequest request);

    @GET("/products/")
        Call<List<Produ>> getProductos();
}
