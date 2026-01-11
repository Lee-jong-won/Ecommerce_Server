package jongwon.e_commerce.order.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {

    private Long productId;
    private int stockQuantity;

    public OrderItemRequest(Long productId, int stockQuantity){
        this.productId = productId;
        this.stockQuantity = stockQuantity;
    }
}
