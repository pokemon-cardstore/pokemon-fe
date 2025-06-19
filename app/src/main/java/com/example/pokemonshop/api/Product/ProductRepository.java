package com.example.pokemonshop.api.Product;

import com.example.pokemonshop.api.APIClient;

public class ProductRepository {
    public static ProductService getProductService(){
        return APIClient.getClient().create(ProductService.class);
    }
}
