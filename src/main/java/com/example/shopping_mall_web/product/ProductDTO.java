package com.example.shopping_mall_web.product;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private Integer size;
    private String color;
    private Integer stockQuantity;
    private Long sellerId;
    private BigDecimal price;
    private String category;
    private List<String> images;
    private String contents;
}
