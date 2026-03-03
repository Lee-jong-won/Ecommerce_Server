package jongwon.e_commerce.order.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.product.domain.Product;
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
    private Product product;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(name = "order_quantity", nullable = false)
    private int orderQuantity;

    public void setOrderItemId(long id){
        this.orderItemId = id;
    }
    public int calculateAmount(){
        return orderPrice * orderQuantity;
    }
    public void setOrderEntity(OrderEntity orderEntity){
        this.orderEntity = orderEntity;
    }

    public static OrderItemEntity createOrderItem(Product product,
                                                  String productName,
                                                  int orderPrice,
                                                  int orderQuantity){
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.product = product;
        orderItemEntity.productName = productName;
        orderItemEntity.orderPrice = orderPrice;
        orderItemEntity.orderQuantity = orderQuantity;
        return orderItemEntity;
    }
}
