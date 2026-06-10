package com.back.domain.product.service;

import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProducts() {
        return productRepository.findByDeletedAtIsNull()
                .stream()
                .map(ProductResponseDto::from)
                .toList();

    }

    @Transactional(readOnly = true)
    public Optional<Product> findLatest() {
        return productRepository.findFirstByDeletedAtIsNullOrderByIdDesc();
    }

    public Product create(
            String name,
            int price,
            int stock,
            String imgUrl
    ) {
        final Product product = new Product(name, price, stock, imgUrl);

        return productRepository.save(product);
    }

    public Product update(Long id, String name, Integer price, Integer stock, String imgUrl) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을수 없습니다."));
        product.update(name, price, stock, imgUrl);

        return product;
    }

    public void delete(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new RuntimeException("해당 상품을 찾을수 없습니다."));

        product.delete();
    }
}
