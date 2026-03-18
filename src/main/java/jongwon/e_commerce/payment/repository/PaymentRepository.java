package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;

import java.util.Optional;

public interface PaymentRepository {
    Pay save(Pay pay);
    Pay saveDetail(Pay pay);
    Optional<PayEntity> findById(long id);
}
