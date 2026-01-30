package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByPayOrderId(String payOrderId);
}
