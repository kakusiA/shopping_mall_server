package com.example.shopping_mall_web.cart;

import lombok.Data;

@Data
public class CartItemDto {
    private Long productId;
    private int quantity;
}