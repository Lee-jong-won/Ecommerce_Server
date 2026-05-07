package jongwon.e_commerce.payment.infrastructure.jpa;


import jongwon.e_commerce.payment.infrastructure.jpa.entity.PayEntity;
import jongwon.e_commerce.payment.infrastructure.jpa.entity.PayRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PayEntity, Long> {
    @Query("select p from PayEntity p " +
    "where p.payRequestEntity.payRequestId = :payRequestId")
    Optional<PayEntity> findByPayRequestId(Long payRequestId);
}
