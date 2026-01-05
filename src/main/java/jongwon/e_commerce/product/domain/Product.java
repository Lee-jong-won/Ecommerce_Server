package jongwon.e_commerce.product.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.product.exception.InvalidProductPriceException;
import jongwon.e_commerce.product.exception.NotEnoughStockException;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private int productPrice;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //기본 생성자
    private Product() {
    }

    // 새로운 상품 추가시 사용되는 생성자
    private Product(String productName, int productPrice, int stockQuantity){
        this.productName = productName;
        this.productPrice = productPrice;
        this.stockQuantity = stockQuantity;
    }

    // 새로운 상품 생성은 항상 이 메소드를 통해서만
    public static Product create(String productName, int productPrice, int stockQuantity){
        return new Product(productName, productPrice, stockQuantity);
    }

    // 재고 추가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    // 재고 감소
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0)
            throw new NotEnoughStockException("need more stock");
        this.stockQuantity = restStock;
    }

    // 가격 업데이트
    public void updateProductPrice(int price){
        if(price <= 0)
            throw new InvalidProductPriceException("price cannot be negative or zero");
        this.productPrice = price;
    }
}
