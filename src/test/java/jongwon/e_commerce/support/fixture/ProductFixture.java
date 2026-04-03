package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductFixture {

    @Builder.Default
    private String productName = "기본 상품";

    @Builder.Default
    private int productPrice = 10000;

    @Builder.Default
    private ProductStatus productStatus = ProductStatus.READY;

    @Builder.Default
    private int stockQuantity = 100;

    public Product create() {
        return Product.builder()
                .productName(productName)
                .productPrice(productPrice)
                .productStatus(productStatus)
                .stockQuantity(stockQuantity)
                .build();
    }

    public static Product createLaptop(){
        return ProductFixture.
                builder().
                productName("노트북").
                productPrice(100000).
                build().
                create();
    }

    public static Product createMouse(){
        return ProductFixture.
                builder().
                productName("마우스").
                productPrice(50000).
                build().
                create();
    }
}
