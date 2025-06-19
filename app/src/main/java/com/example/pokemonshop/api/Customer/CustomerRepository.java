package com.example.pokemonshop.api.Customer;

import com.example.pokemonshop.api.APIClient;
import com.example.pokemonshop.api.ApiResponse;
import com.example.pokemonshop.model.Customer;

import retrofit2.Call;


public class CustomerRepository {
    public static CustomerService getCustomerService() {
        return APIClient.getClient().create(CustomerService.class);
    }

    public static Call<ApiResponse> updateCustomerInfo(int customerId, Customer customer) {
        return getCustomerService().updateCustomerInfo(customerId, customer);
    }
}
