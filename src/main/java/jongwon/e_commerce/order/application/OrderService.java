package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.controller.Cart;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCreator orderCreator;

    @Transactional
    public Order placeOrder(Member member, String orderId, Cart cart){
        Order order = orderCreator.mapFrom(member, orderId, cart);
        order.place();
        return orderRepository.save(order);
    }
}
