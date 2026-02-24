package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.payment.domain.MobilePhonePay;
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
    public MobilePhonePay save(MobilePhonePay mobilePhonePay) {
        return mobilePhonePayDetailJpaRepository.save(mobilePhonePay);
    }

    @Override
    public Optional<MobilePhonePay> findById(Long id) {
        return mobilePhonePayDetailJpaRepository.findById(id);
    }
}
