package jongwon.e_commerce.order.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem extends BaseEntity {
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

    public static OrderItem createOrderItem(Long orderId, Long productId, String productName, int orderPrice, int orderQuantity){
        OrderItem orderItem = new OrderItem();
        orderItem.orderId = orderId;
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.orderPrice = orderPrice;
        orderItem.orderQuantity = orderQuantity;
        return orderItem;
    }
}
