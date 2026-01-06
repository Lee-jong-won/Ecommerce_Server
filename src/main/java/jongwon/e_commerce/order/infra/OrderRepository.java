package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
