package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.repository.MPPayRepository;
import jongwon.e_commerce.payment.repository.jpa.MPPayJpaRepository;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class MPPayRepositoryimpl implements MPPayRepository {

    private final MPPayJpaRepository mpPayJpaRepository;

    @Override
    public MPPay save(MPPay mpPay) {
        return mpPayJpaRepository.save(MPPayEntity.from(mpPay)).toModel();
    }

    @Override
    public Optional<MPPay> findByPay(Pay pay) {
        return mpPayJpaRepository.findByPayEntity(PayEntity.from(pay)).map(MPPayEntity::toModel);
    }

    @Override
    public MPPay getByPay(Pay pay) {
        return findByPay(pay).orElseThrow(
                () -> new ResourceNotFoundException("Does not Exist mpPay of PayId:", pay.getId())
        );
    }
}
