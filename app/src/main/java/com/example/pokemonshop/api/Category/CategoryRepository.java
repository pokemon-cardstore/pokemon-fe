package com.example.pokemonshop.api.Category;

import com.example.pokemonshop.api.APIClient;

public class CategoryRepository {
    public static CategoryService getCategoryService(){
        return APIClient.getClient().create(CategoryService.class);
    }
}
