package com.dsi.smartpajak.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    @POST("login")
    @FormUrlEncoded
    Call<APICallback> login(@Field("username") String username, @Field("password") String password);

    @GET("skpd")
    Call<APICallback> skpd(@Query("key") String key, @Query("page") int page);

    @GET("tunggakan")
    Call<APICallback> tunggakan(@Query("key") String key);

    @GET("sspd")
    Call<APICallback> sspd(@Query("key") String key, @Query("page") int page);

    @GET("pbb")
    Call<PBBCallback> pbb(@Query("number") String number, @Query("year") int year);

    @GET("pbb-detail")
    Call<PBBCallback> pbbDetail(@Query("number") String number);

    @GET("how-to-pay")
    Call<APICallback> howToPay();

    @GET("url")
    Call<JsonObject> getUrl();

    @GET("bea-ketetapan")
    Call<JsonObject> getBeaKetetapan();

    @GET("jenis-hak")
    Call<JsonObject> getJenisHak();

    @GET("jenis-perolehan")
    Call<JsonObject> getJenisPerolehan();

    @GET("sosmed")
    Call<APICallback> sosmed();

    @POST("register-fcm-token")
    @FormUrlEncoded
    Call<APICallback> registerFCMToken(
            @Field("key") String api_key,
            @Field("token") String token,
            @Field("device_name") String device_name,
            @Field("device_serial") String device_serial,
            @Field("device_os") String device_os
    );
}