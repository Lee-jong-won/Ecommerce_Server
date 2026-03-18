package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;

import java.util.Optional;

public interface PaymentRepository {
    Pay save(Pay pay);
    Optional<Pay> findById(long id);
    Pay getById(Long id);
}
