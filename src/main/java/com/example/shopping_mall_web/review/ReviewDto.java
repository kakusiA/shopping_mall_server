package com.example.shopping_mall_web.review;


import lombok.Data;

@Data
public class ReviewDto {
    private Long reviewId;
    private Long productId;
    private int rating;
    private String reviewText;
    private Long userId;
}
