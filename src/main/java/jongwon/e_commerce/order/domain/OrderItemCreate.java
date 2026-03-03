package jongwon.e_commerce.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OrderItemCreate {

    private Long productId;
    private int stockQuantity;

    @Builder
    public OrderItemCreate(
            @JsonProperty("productId") Long productId,
            @JsonProperty("stockQuantity") int stockQuantity){
        this.productId = productId;
        this.stockQuantity = stockQuantity;
    }

}
