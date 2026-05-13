package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.controller.Cart;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderCreator {

    private final ProductRepository productRepository;

    public Order mapFrom(Member member,
                       String orderId,
                       Cart cart){
        // 주문 생성
        Order order = Order.createOrder(member,
                LocalDateTime.now(),
                orderId,
                cart.getCartLineItems().stream().map(this::toOrderItem).collect(Collectors.toList()),
                cart.getOrderName());

        return order;
    }

    private OrderItem toOrderItem(Cart.CartLineItem cartLineItem){
        int stockQuantity = cartLineItem.getStockQuantity();
        Product product = productRepository.getById(cartLineItem.getProductId());

        return OrderItem.createOrderItem(product, stockQuantity);
    }
}
