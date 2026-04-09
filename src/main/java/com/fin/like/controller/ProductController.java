package com.fin.like.controller;

import com.fin.like.common.response.ApiResponse;
import com.fin.like.dto.ProductDto;
import com.fin.like.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductDto>> getAllProducts() {
        return ApiResponse.success(productService.getAllProducts());
    }

    @GetMapping("/{productName}")
    public ApiResponse<ProductDto> getProductByName(@PathVariable String productName) {
        return ApiResponse.success(productService.getProductByName(productName));
    }
}