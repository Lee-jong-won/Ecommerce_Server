package jongwon.e_commerce.payment.repository.jpa;

import jongwon.e_commerce.payment.domain.MobilePhonePayDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobilePhonePayDetailJpaRepository extends JpaRepository<MobilePhonePayDetail, Long> {
}
