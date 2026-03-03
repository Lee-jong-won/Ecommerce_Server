package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;

import java.util.Optional;

public interface OrderRepository {
    OrderEntity save(OrderEntity orderEntity);
    Optional<OrderEntity> findById(Long id);
    Optional<OrderEntity> findByOrderId(String orderId);
    void clearStore();
}
