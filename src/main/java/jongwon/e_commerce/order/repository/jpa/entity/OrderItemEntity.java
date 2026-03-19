package jongwon.e_commerce.order.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.jpa.ProductEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "fk_order_id")
    private OrderEntity orderEntity;

    @ManyToOne
    @JoinColumn(name = "fk_product_id")
    private ProductEntity productEntity;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(name = "order_quantity", nullable = false)
    private int orderQuantity;

    public static OrderItemEntity from(OrderItem orderItem){
        OrderItemEntity orderItemEntity = new OrderItemEntity();

        orderItemEntity.orderItemId = orderItem.getOrderItemId();
        orderItemEntity.orderEntity = OrderEntity.from(orderItem.getOrder());
        orderItemEntity.productEntity = ProductEntity.from(orderItem.getProduct());
        orderItemEntity.orderPrice = orderItem.getOrderPrice();
        orderItemEntity.productName = orderItem.getProductName();
        orderItemEntity.orderQuantity = orderItem.getOrderQuantity();

        return orderItemEntity;
    }

    public OrderItem toModel(){
        return OrderItem.builder().
                orderItemId(orderItemId).
                order(orderEntity.toModel()).
                product(productEntity.toModel())
                .productName(productName)
                .orderPrice(orderPrice)
                .orderQuantity(orderQuantity)
                .build();
    }
}
