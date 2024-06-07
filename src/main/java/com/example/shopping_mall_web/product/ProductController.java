package com.example.shopping_mall_web.product;

import com.example.shopping_mall_web.user.CustomUserDetails;
import com.example.shopping_mall_web.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 모든 상품 목록을 조회하는 공용 메서드 (인증 없이 접근 가능)
    @GetMapping("/public")
    public ResponseEntity<List<ProductDTO>> getAllPublicProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 모든 상품 목록을 조회하는 메서드 (인증 필요)
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 새로운 상품을 추가하는 메서드 (인증 필요)
    @PostMapping("/create")
    public ResponseEntity<List<ProductDTO>> addProducts(@RequestBody ProductDTO[] productDTOs, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDetails.getUser();
        Long sellerId = user.getUserId();

        List<ProductDTO> addedProducts = new ArrayList<>();
        for (ProductDTO productDTO : productDTOs) {
            productDTO.setSellerId(sellerId);
            ProductDTO addedProduct = productService.addProduct(productDTO);
            addedProducts.add(addedProduct);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(addedProducts);
    }

    // 특정 ID의 상품을 업데이트하는 메서드 (인증 필요)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 특정 ID의 상품을 삭제하는 메서드 (인증 필요)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
