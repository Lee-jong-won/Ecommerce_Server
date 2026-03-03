package jongwon.e_commerce.order.repository.jpa;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findOrderItemsBy(OrderEntity orderEntity);
}
