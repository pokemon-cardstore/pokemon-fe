package com.example.pokemonshop.model;

import java.util.List;

public class ChatHistoryResponse {
    private String customerName;
    private List<MessageDtoResponse> messageHistory;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<MessageDtoResponse> getMessageHistory() {
        return messageHistory;
    }

    public void setMessageHistory(List<MessageDtoResponse> messageHistory) {
        this.messageHistory = messageHistory;
    }
}
