package com.example.shopping_mall_web.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    public ProductDTO addProduct(ProductDTO productDTO) {
        // 상품 생성 전 유효성 검사 및 필요한 로직 수행
        if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        if (productDTO.getProductImg() == null || productDTO.getProductImg().isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }
        if (productDTO.getCategory() == null || productDTO.getCategory().isEmpty()) {
            throw new IllegalArgumentException("상품 카테고리는 필수입니다.");
        }

        Product product = convertToEntity(productDTO);
        product.setSellerId(productDTO.getSellerId()); // seller_id 값 설정
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        if (productRepository.existsById(id)) {
            Product product = convertToEntity(productDTO);
            product.setProductId(id);
            Product updatedProduct = productRepository.save(product);
            return convertToDTO(updatedProduct);
        }
        return null;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setSize(product.getSize());
        productDTO.setColor(product.getColor());
        productDTO.setStockQuantity(product.getStockQuantity());
        productDTO.setSellerId(product.getSellerId());
        productDTO.setPrice(product.getPrice());
        productDTO.setProductImg(product.getProductImg());
        productDTO.setCategory(product.getCategory());
        return productDTO;
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setSize(productDTO.getSize());
        product.setColor(productDTO.getColor());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setSellerId(productDTO.getSellerId());
        product.setPrice(productDTO.getPrice());
        product.setProductImg(productDTO.getProductImg());
        product.setCategory(productDTO.getCategory());
        return product;
    }
}
