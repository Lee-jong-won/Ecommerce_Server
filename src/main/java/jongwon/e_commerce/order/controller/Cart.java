package jongwon.e_commerce.order.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Cart {

    private final String orderName;
    private final String loginId;
    private final List<CartLineItem> cartLineItems;
    @Builder
    public Cart(@JsonProperty("orderName")String orderName,
                @JsonProperty("loginId")String loginId,
                @JsonProperty("orderCreates")List<CartLineItem> cartLineItems
                       ){
        this.orderName = orderName;
        this.loginId = loginId;
        this.cartLineItems = cartLineItems;
    }

    @Getter
    public static class CartLineItem {
        private Long productId;
        private String productName;
        private int orderPrice;
        private int stockQuantity;

        @Builder
        public CartLineItem(
                @JsonProperty("productId") Long productId,
                @JsonProperty("productName") String productName,
                @JsonProperty("orderPrice") int orderPrice,
                @JsonProperty("stockQuantity") int stockQuantity){
            this.productId = productId;
            this.productName = productName;
            this.orderPrice = orderPrice;
            this.stockQuantity = stockQuantity;
        }
    }
}
