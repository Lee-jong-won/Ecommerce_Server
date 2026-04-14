package jongwon.e_commerce.payment.repository.jpa;

import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MPPayJpaRepository extends JpaRepository<MPPayEntity, Long> {
    Optional<MPPayEntity> findByPayEntity(PayEntity payEntity);
}
