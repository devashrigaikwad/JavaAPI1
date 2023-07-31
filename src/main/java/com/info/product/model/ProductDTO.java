package com.info.product.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductDTO {
    private String name;
    private BigDecimal price;
    private ProductStatus status;

}
