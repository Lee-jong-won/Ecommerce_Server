package jongwon.e_commerce.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreate {

    private final String orderName;
    private final List<OrderItemCreate> orderItemCreates;

    @Builder
    public OrderCreate(@JsonProperty("orderName")String orderName,
                       @JsonProperty("orderCreates")List<OrderItemCreate> orderItemCreates
                       ){
        this.orderName = orderName;
        this.orderItemCreates = orderItemCreates;
    }
}
