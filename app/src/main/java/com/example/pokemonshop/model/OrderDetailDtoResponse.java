package com.example.pokemonshop.model;


import java.io.Serializable;

public class OrderDetailDtoResponse implements Serializable {
    private int productId;
    private String productName;
    private double pricePerUnit;
    private int quantity;

    public OrderDetailDtoResponse(int productId, double pricePerUnit, int quantity) {
        this.productId = productId;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    // Getter và Setter cho productName
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "OrderDetailDtoResponse{" +
                "productId=" + productId +
                ", pricePerUnit=" + pricePerUnit +
                ", quantity=" + quantity +
                '}';
    }
}
