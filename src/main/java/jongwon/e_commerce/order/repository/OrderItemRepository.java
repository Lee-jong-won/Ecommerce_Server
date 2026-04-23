package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
    List<OrderItem> findByOrderId(Long orderId);
}
