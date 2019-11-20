package com.example.myapplication.Interface;

import com.example.myapplication.Models.Provider;
import com.example.myapplication.Models.ProviderReserves;
import com.example.myapplication.Models.Reserves;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolder {

    /*@GET("user")
    Call<List<User>> getUser();*/

    @GET("getReserves/{id}")
    Call<List<Reserves>> getReservation(@Path("id") String userId);

    @GET("getUsersReserves/{id}")
    Call<List<ProviderReserves>> getUsersReserves(@Path("id") String providerId);

    @GET("auth/user")
    Call<ResponseBody> getUser(@Header("Authorization") String header);

    @GET("getProviders")
    Call<List<Provider>> getLatLng();

    @GET("sendAccept/{id}")
    Call<ResponseBody> sendAccept(@Path("id") Integer id);

    @GET("sendDecline/{id}")
    Call<ResponseBody> sendDecline(@Path("id") Integer id);

    @GET("deleteReserve/{id}")
    Call<ResponseBody> deleteReserve(@Path("id") String reserveId);

    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> loginPost(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/signup")
    Call<ResponseBody> register(
            @Field("email") String email,
            @Field("password") String password,
            @Field("password_confirmation") String passwordConfirmation,
            @Field("name") String userName,
            @Field("device_token") String deviceToken
    );

    @FormUrlEncoded
    @POST("reserve")
    Call<ResponseBody> reserve(
            @Field("time") String time,
            @Field("date") String date,
            @Field("provider_id") String provider_id,
            @Field("user_id") String user_id
    );
}
