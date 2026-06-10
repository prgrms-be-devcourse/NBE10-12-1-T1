package com.back.domain.product.repository;

import com.back.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //flushAutomatically = true를 통해 update 쿼리 실행 전 영속성 컨텍스트 변경사항 DB에 반영
    //clearAutomatically = true를 통해 update 쿼리 실행 후 영속성 컨텍스트 비워서 최신 DB 가져올 수 있게 만듬
    //JPQL update는 DB를 직접 처리하므로 영속성 컨텍스트의 product는 stock값을 모를 수 있음
    //product 중에서 id가 productId와 같고 삭제되지 않았으며 현재 재고가 주문량 이상이면 "stock - amount"
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update Product p
        set p.stock = p.stock - :amount
        where p.id = :productId
        and p.deletedAt is null
        and p.stock >= :amount
        """)

    int decreaseStockIfAvailable(
            @Param("productId") Long productId,
            @Param("amount") int amount
    );

    boolean existsByIdAndDeletedAtIsNull(Long id);

    List<Product> findByDeletedAtIsNull();

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    Optional<Product> findFirstByDeletedAtIsNullOrderByIdDesc();
}
