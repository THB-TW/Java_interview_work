package com.fin.like.service;

import com.fin.like.dto.ProductDto;
import com.fin.like.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductDto getProductByName(String productName) {
        return productRepository.findByName(productName);
    }
}
