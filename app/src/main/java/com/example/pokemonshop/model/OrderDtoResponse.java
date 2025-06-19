package com.example.pokemonshop.model;

import java.io.Serializable;
import java.util.List;

public class OrderDtoResponse implements Serializable {
    private int orderId;
    private int customerId;
    private double totalPrice;
    private int status;
    private List<OrderDetailDtoResponse> orderDetails;

    public OrderDtoResponse(int orderId, int customerId, double totalPrice, int status, List<OrderDetailDtoResponse> orderDetails) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<OrderDetailDtoResponse> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDtoResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "OrderDtoResponse{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", orderDetails=" + orderDetails +
                '}';
    }
}
