package com.example.shopping_mall_web.product;

import com.example.shopping_mall_web.user.CustomUserDetails;
import com.example.shopping_mall_web.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestPart("postProductReq") ProductDTO productDTO,
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
            return ResponseEntity.ok(product);

        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProductDTO>> getAllPublicProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
//    @GetMapping("/display")
//    public ResponseEntity<byte[]> getImage(String fileName) {
//        File file = new File("D:\\새 폴더\\shopping_mall_web\\uploads\\" + fileName);
//        ResponseEntity<byte[]> result = null;
//
//        try {
//
//            HttpHeaders header = new HttpHeaders();
//
//            header.add("Content-type", Files.probeContentType(file.toPath()));
//
//            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
//
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//
//
//    }
}
