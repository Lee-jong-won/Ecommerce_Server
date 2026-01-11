package jongwon.e_commerce.payment.infra;


import jongwon.e_commerce.payment.domain.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Pay, Long> {
}
