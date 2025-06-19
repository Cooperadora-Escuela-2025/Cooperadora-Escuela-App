package com.example.cooperadora_escuela.network;
import com.example.cooperadora_escuela.models.Product;
import com.example.cooperadora_escuela.models.Purchase;
import com.example.cooperadora_escuela.network.auth.LoginRequest;
import com.example.cooperadora_escuela.network.auth.LoginResponse;
import com.example.cooperadora_escuela.network.auth.RegisterRequest;
import com.example.cooperadora_escuela.network.profile.ProfileRequest;
import com.example.cooperadora_escuela.network.profile.ProfileResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

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

//    @GET("/products/")
//    Call<List<Produ>> getProducts();


    @GET("/products/")
    Call<List<Product>> getProducts();

    @GET("history/")
    Call<List<Purchase>> getPurchaseHistory();

    // crear una cuota
    @POST("cuotas/")
    Call<ResponseBody> crearCuota(@Body RequestBody body);

    // descargar el comprobante en PDF
//    @GET("cuota/{id}/comprobante/")
//    Call<ResponseBody> descargarComprobante(@Path("id") int cuotaId);

    @GET("cuota/{cuota_id}/comprobante/")
    @Streaming
    Call<ResponseBody> descargarComprobante(@Path("cuota_id") int cuotaId);


    //  QR de pago (imagen PNG)
    @GET("cuotas/{cuota_id}/qr/")
    Call<ResponseBody> obtenerQrPago(@Path("cuota_id") int cuotaId);

    // enviar comprobante
    @Multipart
    @POST("enviar-comprobante/")
    Call<ResponseBody> enviarComprobante(
            @Part MultipartBody.Part archivo,
            @Part("alumno_nombre") RequestBody alumnoNombre,
            @Part("alumno_apellido") RequestBody alumnoApellido,
            @Part("alumno_dni") RequestBody alumnoDni,
            @Part("tutor_nombre") RequestBody tutorNombre,
            @Part("tutor_apellido") RequestBody tutorApellido,
            @Part("tutor_dni") RequestBody tutorDni,
            @Part("parentesco") RequestBody parentesco
    );
}