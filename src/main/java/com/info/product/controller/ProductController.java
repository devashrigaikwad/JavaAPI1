package com.info.product.controller;

import com.info.product.model.Product;
import com.info.product.model.ProductDTO;
import com.info.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping("/")
    public ResponseEntity<?> getAllProducts(){
        List<Product> products = productService.getAllProducts();
        log.info("Products found in Service {}", products );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String productName,
                                        @RequestParam(required = false) BigDecimal minPrice,
                                        @RequestParam(required = false) BigDecimal maxPrice,
                                        @RequestParam(required = false) LocalDateTime minPostedDate,
                                        @RequestParam(required = false) LocalDateTime maxPostedDate) {

            return productService.searchProduct(productName, minPrice, maxPrice, minPostedDate, maxPostedDate);
    }

    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            Product createdProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().toString());
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO product) {
        Product productResponse = productService.updateProduct(productId, product);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
