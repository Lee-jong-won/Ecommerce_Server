package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByPayOrderId(String payOrderId);
}
