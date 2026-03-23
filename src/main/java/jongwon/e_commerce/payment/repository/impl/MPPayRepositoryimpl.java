package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.repository.MPPayRepository;
import jongwon.e_commerce.payment.repository.jpa.MPPayJpaRepository;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class MPPayRepositoryimpl implements MPPayRepository {

    private final MPPayJpaRepository mpPayJpaRepository;

    @Override
    public MPPay save(MPPay mpPay) {
        return mpPayJpaRepository.save(MPPayEntity.from(mpPay)).toModel();
    }
}
