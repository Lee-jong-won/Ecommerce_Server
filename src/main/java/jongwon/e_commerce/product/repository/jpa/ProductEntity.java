package jongwon.e_commerce.product.repository.jpa;

import jakarta.persistence.*;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private int productPrice;

    @Column(name = "product_status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    public static ProductEntity from(Product product){
        ProductEntity productEntity = new ProductEntity();

        productEntity.productId = product.getProductId();
        productEntity.productName = product.getProductName();
        productEntity.productPrice = product.getProductPrice();
        productEntity.productStatus = product.getProductStatus();
        productEntity.stockQuantity = product.getStockQuantity();

        return productEntity;
    }

    public Product toModel(){
        return Product.builder().
                productId(productId).
                productName(productName).
                productPrice(productPrice).
                productStatus(productStatus).
                stockQuantity(stockQuantity).build();
    }
}
