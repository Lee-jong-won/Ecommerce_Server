package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.detail.MPPay;

public interface MPPayRepository {
    MPPay save(MPPay mpPay);
}
