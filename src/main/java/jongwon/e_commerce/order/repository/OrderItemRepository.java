package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
    List<OrderItem> findByOrderId(Long orderId);
}
