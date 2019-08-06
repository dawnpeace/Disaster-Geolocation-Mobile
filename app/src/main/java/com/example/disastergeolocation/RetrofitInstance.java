package com.example.disastergeolocation;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static Retrofit getRetrofit(OkHttpClient client){

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(APIUrl.baseUrl)
                .addConverterFactory(GsonConverterFactory.create());
        if(client != null){
            builder.client(client);
        }
        return builder.build();
    }

    public static Retrofit getRetrofit(){
        return getRetrofit(null);
    }
}
