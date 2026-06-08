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
//@Transactional 이걸 쓸까요? 말까요? 쓰면 메소스단에 단순 Transactional은 제거가능
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDto::from)
                .toList();

    }

    //해당 메소드의 기능은 뭐일까요? 테스트용도일까요?
    @Transactional(readOnly = true)
    public Optional<Product> findLatest() {
        return productRepository.findFirstByOrderByIdDesc();
    }


    @Transactional
    public Product create(
            String name,
            int price,
            int stock,
            String imgUrl
    ) {
        final Product product = new Product(name, price, stock, imgUrl);
        // 흐음~ entity를 바로 넘기는건 위험하다고 하네요
        // 저희 수업시간에 post & comment처럼 서로 참조하는 상황일 때
        // 화면에 계속 연쇄적으로 붙어서 출력된 거 기억하실까요? 그런 문제가 발생 할 수 도있고
        // DB값이 그대로 화면으로 노출 될 위험이 있기에 Service단에서 포장까지 다 하고
        // Controller로 넘기는게 더 안전하다고하네용?? 허헣 덕분에 배워갑니다.
        return productRepository.save(product);
    }
}
