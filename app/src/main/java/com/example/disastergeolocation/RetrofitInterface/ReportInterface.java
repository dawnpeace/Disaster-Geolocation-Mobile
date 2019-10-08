package com.example.disastergeolocation.RetrofitInterface;

import com.example.disastergeolocation.Model.HistoryModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ReportInterface {

    @Multipart
    @POST("report")
    Call<Void> sendReport(@Part("lat") RequestBody lat, @Part("lng") RequestBody lng, @Part MultipartBody.Part photo, @Part("target") int target,@Part("android_curtime") RequestBody curtime);

    @GET("history")
    Call<List<HistoryModel>> getHistory();
}
