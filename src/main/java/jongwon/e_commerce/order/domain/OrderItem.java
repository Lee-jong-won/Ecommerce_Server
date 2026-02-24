package jongwon.e_commerce.order.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.product.domain.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "fk_order_id")
    private Order order;

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
    public void setOrder(Order order){
        this.order = order;
    }

    public static OrderItem createOrderItem(Product product,
                                            String productName,
                                            int orderPrice,
                                            int orderQuantity){
        OrderItem orderItem = new OrderItem();
        orderItem.product = product;
        orderItem.productName = productName;
        orderItem.orderPrice = orderPrice;
        orderItem.orderQuantity = orderQuantity;
        return orderItem;
    }
}
