package com.example.pokemonshop.api;

import com.example.pokemonshop.adapters.CustomDateTypeAdapter;
import com.example.pokemonshop.adapters.CustomDateTypeAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    // private static String baseURL = "http://10.0.2.2:5275/api/v1/";

      private static String baseURL = "http://10.0.2.2:5275/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY) // 👈 đảm bảo không tự động lowercase
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
