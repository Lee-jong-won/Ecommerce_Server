package jongwon.e_commerce.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreate {

    String orderName;
    List<OrderCreate> orderCreates;

    @Builder
    public OrderCreate(@JsonProperty("orderName")String orderName,
                       @JsonProperty("orderCreates")List<OrderCreate> orderCreates){
        this.orderName = orderName;
        this.orderCreates = orderCreates;
    }
}
