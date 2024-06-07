package com.example.shopping_mall_web.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCartByUserId(@PathVariable Long userId) {
        CartResponseDto cartResponseDto = cartService.getCartByUserId(userId);
        if (cartResponseDto != null) {
            return ResponseEntity.ok(cartResponseDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartItemDto> addCartItem(@PathVariable Long userId, @RequestBody CartItemDto cartItemDto) {
        CartItemDto addedCartItem = cartService.addCartItem(userId, cartItemDto);
        if (addedCartItem != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCartItem);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
