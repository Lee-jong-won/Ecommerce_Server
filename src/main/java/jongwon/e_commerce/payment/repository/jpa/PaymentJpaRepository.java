package jongwon.e_commerce.payment.repository.jpa;


import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PayEntity, Long> {

    Optional<PayEntity> findByPaymentKey(String paymentKey);

}
