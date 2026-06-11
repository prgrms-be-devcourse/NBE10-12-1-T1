package com.back.domain.product.service;

import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import com.back.global.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductResponseDto> getProducts() {
        return productRepository.findByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(ProductResponseDto::from)
                .toList();

    }

    public Optional<Product> findLatest() {
        return productRepository.findFirstByDeletedAtIsNullOrderByIdDesc();
    }

    @Transactional
    public Product create(
            String name,
            int price,
            int stock,
            String imgUrl
    ) {
        final Product product = new Product(name, price, stock, imgUrl);

        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, String name, Integer price, Integer stock, String imgUrl) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(ProductNotFoundException::new);
        product.update(name, price, stock, imgUrl);

        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(ProductNotFoundException::new);

        product.delete();
    }
}
