package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItem {

    private Long orderItemId;
    private Order order;
    private Product product;
    private String productName;
    private int orderPrice;
    private int orderQuantity;

    public void setOrder(Order order){
        this.order = order;
    }

    //==생성 메소드==//
    public static OrderItem from(Product product, int orderQuantity){
        return OrderItem.builder().
                product(product).
                productName(product.getProductName()).
                orderPrice(product.getProductPrice()).
                orderQuantity(orderQuantity).build();
    }

    //==비즈니스 로직==//
    public int calculateAmount(){
        return orderPrice * orderQuantity;
    }
}
