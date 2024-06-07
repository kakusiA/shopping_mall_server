package com.example.shopping_mall_web.product;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data // Lombok 어노테이션
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "size")
    private Integer size;

    @Column(name = "color")
    private String color;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "product_img", nullable = false)
    private String productImg;

    @Column(name = "category")
    private String category;

}
