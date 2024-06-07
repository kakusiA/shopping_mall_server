package com.example.shopping_mall_web.review;


import com.example.shopping_mall_web.product.Product;
import com.example.shopping_mall_web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);

    List<Review> findByUser(User user);
}
