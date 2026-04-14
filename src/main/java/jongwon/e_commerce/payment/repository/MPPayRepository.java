package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.MPPay;

import java.util.Optional;

public interface MPPayRepository {
    MPPay save(MPPay mpPay);
    Optional<MPPay> findByPay(Pay pay);
    MPPay getByPay(Pay pay);
}
