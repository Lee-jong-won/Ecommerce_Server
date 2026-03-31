package jongwon.e_commerce.order.controller;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.member.application.MemberService;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.application.OrderExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderCreate;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@Builder
@RequestMapping("/api/order")
public class OrderController {

    private final OrderExecutor orderExecutor;
    @PostMapping
    public ResponseEntity<OrderResponse> order(
            @RequestBody OrderCreate orderCreate,
            @LoginMember Member loginMember // 일반적으로 스프링 시큐리티를 사용한다면 UserPrincipal 에서 가져옵니다.
            ){
        Order order = orderExecutor.order(loginMember,
                orderCreate.getOrderName(), orderCreate.getOrderId(), orderCreate.getOrderItemCreates());
        return ResponseEntity.
                ok().body(OrderResponse.from(order));
    }

}
