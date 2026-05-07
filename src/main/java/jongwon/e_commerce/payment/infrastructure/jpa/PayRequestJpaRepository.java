package jongwon.e_commerce.payment.infrastructure.jpa;

import jongwon.e_commerce.payment.infrastructure.jpa.entity.PayRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayRequestJpaRepository extends JpaRepository<PayRequestEntity, Long> {
    Optional<PayRequestEntity> findByPaymentKey(String paymentKey);

}
