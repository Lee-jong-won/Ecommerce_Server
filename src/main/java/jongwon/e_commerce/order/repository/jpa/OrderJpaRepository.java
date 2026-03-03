package jongwon.e_commerce.order.repository.jpa;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderId(String orderId);
}
