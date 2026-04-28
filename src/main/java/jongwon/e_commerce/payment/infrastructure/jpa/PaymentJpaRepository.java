package jongwon.e_commerce.payment.infrastructure.jpa;


import jongwon.e_commerce.payment.infrastructure.jpa.entity.PayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PayEntity, Long> {

    Optional<PayEntity> findByPaymentKey(String paymentKey);

}
