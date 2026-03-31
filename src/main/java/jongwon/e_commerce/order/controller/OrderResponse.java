package jongwon.e_commerce.order.controller;

import jongwon.e_commerce.member.controller.MemberResponse;
import jongwon.e_commerce.order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {

    private String orderId;
    private MemberResponse member;
    private long totalAmount;

    public static OrderResponse from(Order order){
        return OrderResponse.builder().
                orderId(order.getOrderId()).
                totalAmount(order.getTotalAmount()).
                member(MemberResponse.from(order.getMember())).
                build();
    }
}
