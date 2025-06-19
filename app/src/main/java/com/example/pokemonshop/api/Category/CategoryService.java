package com.example.pokemonshop.api.Category;

import com.example.pokemonshop.model.Category;
import com.example.pokemonshop.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {
    final String CATEGORY = "categories";
    @GET(CATEGORY)
    Call<List<Category>> getAllCategories();

    @GET("Category/{id}")
    Call<Category> find(@Path("id") int id);
    @POST("Category")
    Call<Void> create(@Body Category category);

    @PUT("Category/{id}")
    Call<Void> update(@Body Category category, @Path("id") int id);

    @DELETE("Category/{id}")
    Call<Void> delete(@Path("id") int id);
}
