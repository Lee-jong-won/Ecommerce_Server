package jongwon.e_commerce.support.scenario;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.controller.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PrepareOrderData {

    public Member member;
    public List<Cart.CartLineItem> cartLineItems;

}
