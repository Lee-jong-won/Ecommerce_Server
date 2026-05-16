package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItem {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private int orderPrice;
    private int orderQuantity;

    //==생성 메소드==//
    public static OrderItem createOrderItem(Long productId,
                                            String productName,
                                            int orderPrice,
                                            int orderQuantity){
        return OrderItem.builder().
                productId(productId).
                productName(productName).
                orderPrice(orderPrice).
                orderQuantity(orderQuantity).build();
    }

    //==비즈니스 로직==//
    public int calculateAmount(){
        return orderPrice * orderQuantity;
    }

}
