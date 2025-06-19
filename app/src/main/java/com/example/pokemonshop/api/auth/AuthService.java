package com.example.pokemonshop.api.auth;

import com.example.pokemonshop.model.Customer;
import com.example.pokemonshop.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("Auth")
    Call<LoginResponse> login(@Query("email") String email, @Query("password") String password);

    @POST("Customer")
    Call<Void> register(@Body Customer customer);
}
