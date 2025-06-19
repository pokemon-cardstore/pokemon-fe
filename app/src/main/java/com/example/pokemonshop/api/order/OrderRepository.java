package com.example.pokemonshop.api.order;

import com.example.pokemonshop.api.APIClient;

public class OrderRepository {
    public static OrderService getOrderService(){
        return APIClient.getClient().create(OrderService.class);
    }


}
