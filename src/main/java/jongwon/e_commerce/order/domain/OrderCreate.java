package jongwon.e_commerce.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreate {

    private final String orderName;
    private final String orderId;
    private final List<OrderItemCreate> orderItemCreates;

    @Builder
    public OrderCreate(@JsonProperty("orderName")String orderName,
                       @JsonProperty("orderId")String orderId,
                       @JsonProperty("orderCreates")List<OrderItemCreate> orderItemCreates
                       ){
        this.orderName = orderName;
        this.orderId = orderId;
        this.orderItemCreates = orderItemCreates;
    }
}
