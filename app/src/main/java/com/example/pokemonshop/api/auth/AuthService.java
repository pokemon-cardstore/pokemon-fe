package com.example.pokemonshop.api.auth;

import com.example.pokemonshop.model.Customer;
import com.example.pokemonshop.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.pokemonshop.model.RegisterDto;
import com.google.gson.JsonObject;

public interface AuthService {
    @POST("Auth")
    Call<LoginResponse> login(@Query("email") String email, @Query("password") String password);

    @POST("Register")
    Call<Void> register(@Body RegisterDto dto);

    @GET("Activate")
    Call<String> activateAccount(@Query("email") String email);


    @POST("auth/google-login")
    Call<LoginResponse> loginWithGoogle(@Body JsonObject idTokenBody);

}
