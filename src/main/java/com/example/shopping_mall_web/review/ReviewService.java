package com.example.shopping_mall_web.review;

import com.example.shopping_mall_web.product.Product;
import com.example.shopping_mall_web.product.ProductRepository;
import com.example.shopping_mall_web.user.User;
import com.example.shopping_mall_web.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Review> getReviewsByProductId(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.map(reviewRepository::findByProduct).orElse(null);
    }

    public Review addReview(ReviewDto reviewDto) {
        Optional<Product> product = productRepository.findById(reviewDto.getProductId());
        Optional<User> user = userRepository.findById(reviewDto.getUserId());
        if (product.isPresent() && user.isPresent()) {
            Review review = new Review();
            review.setProduct(product.get());
            review.setUser(user.get());
            review.setReviewText(reviewDto.getReviewText());
            review.setRating(reviewDto.getRating());
            return reviewRepository.save(review);
        }
        return null;
    }

    public List<ReviewDto> getReviewsByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Review> reviews = reviewRepository.findByUser(user.get());
            return reviews.stream().map(this::convertToDto).collect(Collectors.toList());
        }
        return null;
    }

    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setProductId(review.getProduct().getProductId());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setUserId(review.getUser().getUserId());
        return dto;
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // Remove the likeReview and dislikeReview methods
}
