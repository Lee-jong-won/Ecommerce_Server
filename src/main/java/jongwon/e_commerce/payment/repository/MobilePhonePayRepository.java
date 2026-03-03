package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.detail.MPPay;

import java.util.Optional;

public interface MobilePhonePayRepository {
    MPPay save(MPPay mpPay);
    Optional<MPPay> findById(Long id);
}
