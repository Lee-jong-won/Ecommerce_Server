package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
