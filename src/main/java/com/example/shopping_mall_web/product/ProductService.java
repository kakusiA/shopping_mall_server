package com.example.shopping_mall_web.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    // 절대 경로 사용
    private static final String UPLOAD_DIR = "D:/새 폴더/shopping_mall_web/uploads/";

    public Product saveProduct(ProductDTO productDTO, List<MultipartFile> uploadFiles) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        if (productDTO.getCategory() == null || productDTO.getCategory().isEmpty()) {
            throw new IllegalArgumentException("상품 카테고리는 필수입니다.");
        }

        if (uploadFiles != null) {
            for (MultipartFile file : uploadFiles) {
                String fileName = file.getOriginalFilename();
                if (fileName != null) {
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, file.getBytes());
                    imageUrls.add("/uploads/" + fileName.replace("\\", "/"));
                }
            }
        }

        Product product = new Product();
        product.setSellerId(productDTO.getSellerId());
        product.setName(productDTO.getName());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setSize(productDTO.getSize());
        product.setColor(productDTO.getColor());
        product.setContents(productDTO.getContents());
        product = productRepository.save(product);

        final Product savedProduct = product;
        List<ProductImage> images = imageUrls.stream()
                .map(url -> new ProductImage(savedProduct, url))
                .collect(Collectors.toList());
        savedProduct.setImages(images);

        return productRepository.save(savedProduct);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::convertToDTO).toList();
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO(product);
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setImages(product.getImages().stream().map(ProductImage::getImageUrl).toList());
        return dto;
    }
}
