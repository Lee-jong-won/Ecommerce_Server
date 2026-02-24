package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.MobilePhonePay;

import java.util.Optional;

public interface MobilePhonePayRepository {
    MobilePhonePay save(MobilePhonePay pay);
    Optional<MobilePhonePay> findById(Long id);
}
