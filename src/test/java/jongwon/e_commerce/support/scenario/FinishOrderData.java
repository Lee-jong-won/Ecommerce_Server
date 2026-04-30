package jongwon.e_commerce.support.scenario;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FinishOrderData {

    Member member;
    Order order;
    List<OrderItem> orderItems;
}
