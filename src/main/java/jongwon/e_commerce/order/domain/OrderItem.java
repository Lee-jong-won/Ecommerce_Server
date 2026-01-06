package jongwon.e_commerce.order.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "fk_order_id", nullable = false)
    private Long orderId;

    @Column(name = "fk_product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(name = "order_quantity", nullable = false)
    private int orderQuantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private OrderItem(Long productId, String productName, int orderPrice, int orderQuantity){
        this.productId = productId;
        this.productName = productName;
        this.orderPrice = orderPrice;
        this.orderQuantity = orderQuantity;
    }

    public static OrderItem createOrderItem(Long productId, String productName, int orderPrice, int orderQuantity){
        return new OrderItem(productId, productName, orderPrice, orderQuantity);
    }
}
