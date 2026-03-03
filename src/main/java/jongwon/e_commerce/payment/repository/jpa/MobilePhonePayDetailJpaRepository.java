package jongwon.e_commerce.payment.repository.jpa;

import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobilePhonePayDetailJpaRepository extends JpaRepository<MPPayEntity, Long> {
}
