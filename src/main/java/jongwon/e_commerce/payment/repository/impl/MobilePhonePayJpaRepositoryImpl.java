package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import jongwon.e_commerce.payment.repository.MobilePhonePayRepository;
import jongwon.e_commerce.payment.repository.jpa.MobilePhonePayDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class MobilePhonePayJpaRepositoryImpl implements MobilePhonePayRepository {

    private final MobilePhonePayDetailJpaRepository mobilePhonePayDetailJpaRepository;
    @Override
    public MPPay save(MPPay mpPay) {
        return mobilePhonePayDetailJpaRepository.save(MPPayEntity.from(mpPay)).toDomain();
    }

    @Override
    public Optional<MPPay> findById(Long id) {
        return mobilePhonePayDetailJpaRepository.findById(id).map(MPPayEntity::toDomain);
    }
}
