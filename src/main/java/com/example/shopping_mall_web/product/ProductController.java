package com.example.shopping_mall_web.product;

import com.example.shopping_mall_web.user.CustomUserDetails;
import com.example.shopping_mall_web.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestPart("postProductReq") ProductDTO productDTO,
                                           @RequestPart(value = "uploadFiles", required = false) List<MultipartFile> uploadFiles,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = userDetails.getUser();
            Long sellerId = user.getUserId();
            productDTO.setSellerId(sellerId);
            Product product = productService.saveProduct(productDTO, uploadFiles);
            ProductDTO savedProductDTO = new ProductDTO(product);
            return ResponseEntity.ok(new ApiResponse<>(200, "Product created successfully", savedProductDTO));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500, "An error occurred while creating the product", null));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProductDTO>> getAllPublicProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}

class ApiResponse<T> {
    private int code;
    private String message;
    private T result;

    public ApiResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    // 게터, 세터 등 생략
}