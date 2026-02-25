package jongwon.e_commerce.payment.repository.jpa;


import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.jpa.PayJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PayJpaEntity, Long> {
    Optional<PayJpaEntity> findByOrder(Order order);

}
