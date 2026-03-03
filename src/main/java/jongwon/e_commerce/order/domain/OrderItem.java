package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.product.domain.Product;

public class OrderItem {
    private Long orderItemId;
    private OrderEntity orderEntity;
    private Product product;
    private String productName;
    private int orderPrice;
    private int orderQuantity;

    public int calculateAmount(){
        return orderPrice * orderQuantity;
    }
    public void setOrderEntity(OrderEntity orderEntity){
        this.orderEntity = orderEntity;
    }

    public static OrderItem from(Product product,
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
