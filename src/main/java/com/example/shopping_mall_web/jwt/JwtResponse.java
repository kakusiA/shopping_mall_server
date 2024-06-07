package com.example.shopping_mall_web.jwt;

import lombok.Getter;

@Getter
public class JwtResponse {

    private final String jwt;

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }
}