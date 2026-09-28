package com.enoch.bankledger.services;

import com.enoch.bankledger.dto.product.ProductRequest;
import com.enoch.bankledger.dto.product.ProductResponse;
import com.enoch.bankledger.entity.Product;
import com.enoch.bankledger.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Product code already exists");
        }

        Product product = Product.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .interestRate(request.getInterestRate())
                .transactionFee(request.getTransactionFee())
                .minimumBalance(request.getMinimumBalance())
                .description(request.getDescription())
                .active(true)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    public ProductResponse getProductByCode(String code) {
        Product product = productRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .interestRate(product.getInterestRate())
                .transactionFee(product.getTransactionFee())
                .minimumBalance(product.getMinimumBalance())
                .description(product.getDescription())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}