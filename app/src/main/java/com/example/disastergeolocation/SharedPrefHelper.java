package com.example.disastergeolocation;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.disastergeolocation.Model.UserModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SharedPrefHelper {
    private static SharedPrefHelper mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "SPOTA_ANDROID_2_DOSEN";

    private static final String KEY_USER_TOKEN = "ACCESS_TOKEN";
    private static final String KEY_AUTH_USER = "USER";
    private static final String KEY_FCM_TOKEN = "FIREBASE_TOKEN";
    private static final String KEY_BOOL_FCM_STORED = "FCM_STORED";

    private SharedPrefHelper(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefHelper(context);
        }
        return mInstance;
    }

    public void storeToken(String token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_TOKEN, token);
        editor.apply();
    }

    public void setFcmTokenAvailability(boolean available){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_BOOL_FCM_STORED,available);
        editor.apply();
    }

    public boolean issetFCMToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_BOOL_FCM_STORED,false);
    }

    public String getFirebaseToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FCM_TOKEN,null);
    }

    public void storeFirebaseToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FCM_TOKEN,token);
        editor.apply();
    }


    public void storeUser(UserModel user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(KEY_AUTH_USER, json);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_TOKEN, null) != null;
    }


    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_TOKEN,null);
    }

    public String getUserBody(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AUTH_USER,null);
    }

    public OkHttpClient getInterceptor(){
        final SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + sharedPreferences.getString(KEY_USER_TOKEN,""))
                        .build();
                return chain.proceed(newRequest);
            }
        })
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public UserModel getUser(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_AUTH_USER,null);
        return gson.fromJson(json, UserModel.class);
    }
}
