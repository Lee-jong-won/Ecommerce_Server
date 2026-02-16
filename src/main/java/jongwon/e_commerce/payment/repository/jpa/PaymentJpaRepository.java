package jongwon.e_commerce.payment.repository.jpa;


import jongwon.e_commerce.payment.domain.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Pay, Long> {

    Optional<Pay> findByPaymentKey(String paymentKey);
    Optional<Pay> findByOrderId(String orderId);
    Optional<Pay> findByFkOrderId(Long FkOrderId);

}
