package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);

}
