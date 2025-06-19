package com.example.pokemonshop.api.auth;

import com.example.pokemonshop.api.APIClient;

public class AuthRepository {
    public static AuthService getAuthService(){
        return APIClient.getClient().create(AuthService.class);
    }
}
