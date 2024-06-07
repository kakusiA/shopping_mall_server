package com.example.shopping_mall_web.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDto {
    private Long cartId;
    private Long userId;
    private List<CartItemDto> items;
}