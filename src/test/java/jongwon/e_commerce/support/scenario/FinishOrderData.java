package jongwon.e_commerce.support.scenario;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinishOrderData {

    Member member;
    Order order;
}
