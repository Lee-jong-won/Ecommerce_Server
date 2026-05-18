package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.controller.Cart;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;

    @Transactional
    public Order placeOrder(Long orderRequestId, Cart cart){
        Order order = mapFrom(orderRequestId, cart);
        order.place(orderValidator);
        return orderRepository.save(order);
    }

    private Order mapFrom(Long orderRequesterId, Cart cart){
        // 주문 생성
        Order order = Order.createOrder(orderRequesterId,
                LocalDateTime.now(),
                Order.createOrderId(),
                cart.getCartLineItems().stream().map(this::toOrderItem).collect(Collectors.toList()),
                cart.getOrderName());

        return order;
    }

    private OrderItem toOrderItem(Cart.CartLineItem cartLineItem){
        return OrderItem.createOrderItem(
                cartLineItem.getProductId(),
                cartLineItem.getProductName(),
                cartLineItem.getOrderPrice(),
                cartLineItem.getStockQuantity());
    }
}
