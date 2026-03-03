package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {
    OrderItemEntity save(OrderItemEntity orderItemEntity);

    List<OrderItemEntity> findByOrder(OrderEntity orderEntity);

    Optional<OrderItemEntity> findById(Long id);

    void clearStore();
}
