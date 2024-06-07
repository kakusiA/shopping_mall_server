package com.example.shopping_mall_web.favorite;


import com.example.shopping_mall_web.product.Product;
import com.example.shopping_mall_web.product.ProductRepository;
import com.example.shopping_mall_web.user.User;
import com.example.shopping_mall_web.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Favorite> getFavoritesByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(favoriteRepository::findByUser).orElse(null);
    }

    public Favorite addFavorite(FavoriteDto favoriteDto) {
        Optional<User> user = userRepository.findById(favoriteDto.getUserId());
        Optional<Product> product = productRepository.findById(favoriteDto.getProductId());
        if (user.isPresent() && product.isPresent()) {
            Favorite favorite = new Favorite();
            favorite.setUser(user.get());
            favorite.setProduct(product.get());
            return favoriteRepository.save(favorite);
        }
        return null;
    }

    public void deleteFavorite(Long favoriteId) {
        favoriteRepository.deleteById(favoriteId);
    }
}
