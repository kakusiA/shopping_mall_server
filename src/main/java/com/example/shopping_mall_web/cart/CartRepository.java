package com.example.shopping_mall_web.cart;

import com.example.shopping_mall_web.cart.Cart;
import com.example.shopping_mall_web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}