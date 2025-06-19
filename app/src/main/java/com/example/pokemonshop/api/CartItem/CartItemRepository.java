package com.example.pokemonshop.api.CartItem;

import com.example.pokemonshop.api.APIClient;

public class CartItemRepository {
    public static CartItemService getCartItemService(){
        return APIClient.getClient().create(CartItemService.class);
    }
}
