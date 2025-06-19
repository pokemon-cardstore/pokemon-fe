package com.example.pokemonshop.api.Product;

import com.example.pokemonshop.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("product/{id}")
    Call<Product> find(@Path("id") int id);
    @POST("Products/CreateProduct")
    Call<Void> create(@Body Product product);

    @PUT("product/{id}")
    Call<Void> update(@Body Product product, @Path("id") int id);

    @PUT("product/setStatus/{id}")
    Call<Void> setStatus(@Path("id") int id);

    @GET("Products")
    Call<List<Product>> getProductsByCategory(@Query("CategoryId") int categoryId);

    @GET("product/search/{searchInput}")
    Call<List<Product>> searchProducts(@Path("searchInput") String query);

    @DELETE("product/{id}")
    Call<Void> delete(@Path("id") int id);
}
