package jongwon.e_commerce.order.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Cart {

    private final String orderName;
    private final List<CartLineItem> cartLineItems;
    @Builder
    public Cart(@JsonProperty("orderName")String orderName,
                @JsonProperty("orderCreates")List<CartLineItem> cartLineItems
                       ){
        this.orderName = orderName;
        this.cartLineItems = cartLineItems;
    }

    @Getter
    public static class CartLineItem {
        private Long productId;
        private String name;
        private int stockQuantity;

        @Builder
        public CartLineItem(
                @JsonProperty("productId") Long productId,
                @JsonProperty("name") String name,
                @JsonProperty("stockQuantity") int stockQuantity){
            this.productId = productId;
            this.name = name;
            this.stockQuantity = stockQuantity;
        }
    }
}
