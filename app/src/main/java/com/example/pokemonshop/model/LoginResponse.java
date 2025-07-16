package com.example.pokemonshop.model;

public class LoginResponse {
    private String accessToken;
    private Customer customer;

    public String getAccessToken() {
        return accessToken;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
