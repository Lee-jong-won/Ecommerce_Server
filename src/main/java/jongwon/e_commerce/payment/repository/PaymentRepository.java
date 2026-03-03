package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;

public interface PaymentRepository {
    void save(Pay pay);
}
