package jongwon.e_commerce.product.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.common.domain.BaseEntity;
import jongwon.e_commerce.product.exception.InvalidProductPriceException;
import jongwon.e_commerce.product.exception.InvalidProductStatusException;
import jongwon.e_commerce.product.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private int productPrice;

    @Column(name = "sale_status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    // 새로운 상품 생성은 항상 이 메소드를 통해서만
    public static Product create(String productName, int productPrice) {
        Product product = new Product();
        product.productName = productName;
        product.productPrice = productPrice;
        product.productStatus = ProductStatus.READY;
        return product;
    }

    // 재고 추가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    // 재고 감소
    public void removeStock(int quantity){
        if (this.productStatus != ProductStatus.SELLING) {
            throw new InvalidProductStatusException("판매중인 상품만 재고 차감 가능");
        }

        int restStock = this.stockQuantity - quantity;
        if(restStock < 0)
            throw new NotEnoughStockException("재고가 충분하지 않습니다");
        this.stockQuantity = restStock;

        if(stockQuantity == 0)
            stopSelling();
    }

    // 가격 업데이트
    public void updateProductPrice(int price){
        if(price <= 0)
            throw new InvalidProductPriceException("price cannot be negative or zero");
        this.productPrice = price;
    }

    public void setStatus(ProductStatus status) {
        this.productStatus = status;
    }

    public void changeStatus(ProductStatus newStatus){
        if(this.productStatus == newStatus)
            return;
        this.productStatus = newStatus;
    }

    public void startSelling() {
        if (this.productStatus != ProductStatus.READY && this.productStatus != ProductStatus.STOPPED) {
            throw new InvalidProductStatusException("판매 시작 불가능 상태입니다.");
        }
        if (stockQuantity <= 0) {
            throw new NotEnoughStockException("재고가 없으면 판매 불가");
        }
        changeStatus(ProductStatus.SELLING);
    }

    public void stopSelling() {
        if (this.productStatus != ProductStatus.SELLING) {
            throw new InvalidProductStatusException("판매중 상태에서만 판매중지 가능");
        }
        changeStatus(ProductStatus.STOPPED);
    }

    public void resumeSelling() {
        if (this.productStatus != ProductStatus.STOPPED) {
            throw new InvalidProductStatusException("판매중지 상태에서만 재개 가능");
        }
        if (stockQuantity <= 0) {
            throw new InvalidProductStatusException("재고가 없으면 재개 불가");
        }
        changeStatus(ProductStatus.SELLING);
    }

    public void endSelling() {
        if (this.productStatus != ProductStatus.STOPPED) {
            throw new InvalidProductStatusException("판매중지 상태에서만 판매종료 가능");
        }
        changeStatus(ProductStatus.ENDED);
    }

    public void changeBasicInfo(String name, int price) {
        if (this.productStatus != ProductStatus.READY && this.productStatus != ProductStatus.STOPPED) {
            throw new InvalidProductStatusException("상품 수정 불가능 상태");
        }
        this.productName = name;
        this.productPrice = price;
    }

    public void changeStock(int stockQuantity) {
        if (this.productStatus != ProductStatus.READY && this.productStatus != ProductStatus.STOPPED) {
            throw new InvalidProductStatusException("재고 수정 불가 상태");
        }
        if (stockQuantity < 0) throw new IllegalArgumentException();
        this.stockQuantity = stockQuantity;
    }

    public boolean validateDeletable() {
        if (this.productStatus != ProductStatus.READY)
            return false;
        return true;
    }


}
