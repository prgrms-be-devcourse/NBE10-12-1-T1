package com.back.domain.product.entity;

import com.back.global.exception.InsufficientStockException;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column
    private LocalDateTime deletedAt;

    private String imgUrl;

    public Product(String name, int price, int stock, String imgUrl) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imgUrl = imgUrl;
    }

    public void update(String name, Integer price, Integer stock, String imgUrl) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (stock != null) this.stock = stock;
        if (imgUrl != null) this.imgUrl = imgUrl;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void decreaseStock(Integer amount) {
        if (amount == null|| amount <=0) {
            throw new IllegalArgumentException("최소 수량은 1개 이상입니다.");
        }
        if(this.stock < amount) {
            throw new InsufficientStockException();
        }
        this.stock -= amount;
    }
}