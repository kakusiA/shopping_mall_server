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

    public ProductDTO() {
    }

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.size = product.getSize();
        this.color = product.getColor();
        this.stockQuantity = product.getStockQuantity();
        this.sellerId = product.getSellerId();
        this.price = product.getPrice();
        this.category = product.getCategory();
        this.contents = product.getContents();
    }
}