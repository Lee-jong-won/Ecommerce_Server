package jongwon.e_commerce.order.infrastructure.jpa;

import jongwon.e_commerce.order.infrastructure.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    @Transactional(readOnly = true)
    Optional<OrderEntity> findByOrderId(String orderId);
}
