package com.example.shopping_mall_web.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private Integer size;
    private String color;
    private Integer stockQuantity;
    private Long sellerId;
    private BigDecimal price;
    private String productImg;
    private String category;
}
