package com.example.pokemonshop.api.Customer;

import com.example.pokemonshop.api.ApiResponse;
import com.example.pokemonshop.model.Customer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CustomerService {
    @GET("Customer/{id}")
    Call<Customer> getCustomerInfomation(@Path("id") int id);

    @GET("Customer")
    Call<List<Customer>> getCustomers();

    @PUT("Customer/{id}")
    Call<ApiResponse> updateCustomerInfo(@Path("id") int id, @Body Customer customer);
}
