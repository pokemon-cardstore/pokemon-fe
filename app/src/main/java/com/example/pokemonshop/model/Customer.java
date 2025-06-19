package com.example.pokemonshop.model;

public class Customer {
    private int customerId;
    private String email;
    private String password;
    private String name;
    private String address;
    private String phone;
    private String doB;

    // Existing constructors
    public Customer(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Customer(String email, String password, String name, String address, String phone, String doB) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.doB = doB;
    }

    public Customer() {
    }

    // New constructor
    public Customer(String email, String name, String address, String phone, String doB) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.doB = doB;
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDoB() {
        return doB;
    }

    public void setDoB(String doB) {
        this.doB = doB;
    }
}
