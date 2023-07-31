package com.info.product.service;

import com.info.approval.model.ApprovalQueue;
import com.info.approval.repository.ApprovalQueueRepository;
import com.info.product.model.Product;
import com.info.product.model.ProductDTO;
import com.info.product.model.ProductStatus;
import com.info.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ApprovalQueueRepository approvalQueueRepository;

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findByActiveTrueOrderByCreatedAtDesc();
        log.info("Products found in DB {}", products);
        return products;
    }
    public List<Product> searchProduct(
            String productName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDateTime minPostedDate,
            LocalDateTime maxPostedDate) {
        List<Product> products = productRepository
                .searchProducts(productName, minPrice, maxPrice, minPostedDate, maxPostedDate);
        log.info("Products found in DB {}", products);
        return products;
    }
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        validateProduct(productDTO);

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setActive(productDTO.getStatus());

        if (productDTO.getPrice().compareTo(new BigDecimal("5000")) > 0) {
             pushToApprovalQueue(product);
        }else {
            productRepository.save(product);
        }
        return product;
    }

    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = getProductById(productId);
        validateProduct(productDTO);

        BigDecimal previousPrice = existingProduct.getPrice();
        BigDecimal newPrice = productDTO.getPrice();

        if (newPrice.compareTo(previousPrice.multiply(new BigDecimal("1.5"))) > 0) {
             pushToApprovalQueue(existingProduct);
        } else {
            existingProduct.setName(productDTO.getName());
            existingProduct.setPrice(newPrice);
            existingProduct.setActive(productDTO.getStatus());
            productRepository.save(existingProduct);
        }
        return existingProduct;
    }

    public void deleteProduct(Long productId) {
        Product existingProduct = getProductById(productId);
        pushToApprovalQueue(existingProduct);

        existingProduct.setActive(ProductStatus.INACTIVE);
        productRepository.save(existingProduct);
    }

    private void validateProduct(ProductDTO productDTO) {
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name must not be empty");
        }

        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }

        if (productDTO.getPrice().compareTo(new BigDecimal("10000")) > 0) {
            throw new IllegalArgumentException("Product price must not exceed $10,000");
        }
    }

    @Transactional
    private void pushToApprovalQueue(Product product) {
        productRepository.save(product);

        ApprovalQueue approvalQueue = ApprovalQueue.builder()
                .product(product)
                .requestDate(LocalDateTime.now())
                .build();
        approvalQueueRepository.save(approvalQueue);
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));
    }

}
