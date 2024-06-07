package com.example.shopping_mall_web.favorite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUserId(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestBody FavoriteDto favoriteDto) {
        Favorite favorite = favoriteService.addFavorite(favoriteDto);
        if (favorite != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId) {
        favoriteService.deleteFavorite(favoriteId);
        return ResponseEntity.noContent().build();
    }
}
