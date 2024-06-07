package com.example.shopping_mall_web.cart;

import com.example.shopping_mall_web.product.Product;
import com.example.shopping_mall_web.product.ProductRepository;
import com.example.shopping_mall_web.user.User;
import com.example.shopping_mall_web.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public CartResponseDto getCartByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Cart> cart = cartRepository.findByUser(user.get());
            if (cart.isPresent()) {
                CartResponseDto responseDto = new CartResponseDto();
                responseDto.setCartId(cart.get().getCartId());
                responseDto.setUserId(user.get().getUserId());
                responseDto.setItems(cartItemRepository.findByCart(cart.get()).stream()
                        .map(item -> {
                            CartItemDto itemDto = new CartItemDto();
                            itemDto.setProductId(item.getProduct().getProductId());
                            itemDto.setQuantity(item.getQuantity());
                            return itemDto;
                        }).collect(Collectors.toList()));
                return responseDto;
            }
        }
        return null;
    }

    public CartItemDto addCartItem(Long userId, CartItemDto cartItemDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Cart> cart = cartRepository.findByUser(user.get());
            if (cart.isEmpty()) {
                cart = Optional.of(new Cart());
                cart.get().setUser(user.get());
                cartRepository.save(cart.get());
            }
            Optional<Product> product = productRepository.findById(cartItemDto.getProductId());
            if (product.isPresent()) {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart.get());
                cartItem.setProduct(product.get());
                cartItem.setQuantity(cartItemDto.getQuantity());
                cartItemRepository.save(cartItem);
                return cartItemDto;
            }
        }
        return null;
    }

    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
