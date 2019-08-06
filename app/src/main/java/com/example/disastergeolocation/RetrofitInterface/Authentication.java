package com.example.disastergeolocation.RetrofitInterface;

import com.example.disastergeolocation.Model.AuthModel;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Authentication {
    @FormUrlEncoded
    @POST("auth/login")
    Call<AuthModel> login(@Field("email") String email,@Field("password") String password);

    @Multipart
    @POST("register")
    Call<Void> register(
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("identity_number") RequestBody identity_number,
            @Part("phone") RequestBody phone,
            @Part("gender") RequestBody gender,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part photo
    );
}
