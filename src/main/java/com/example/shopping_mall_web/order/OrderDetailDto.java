package com.example.shopping_mall_web.order;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDto {
    private Long productId;
    private int quantity;
    private BigDecimal pricePerItem;
}
