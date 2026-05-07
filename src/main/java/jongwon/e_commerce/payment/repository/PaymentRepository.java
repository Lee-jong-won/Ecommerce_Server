package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;

import java.util.Optional;

public interface PaymentRepository {
    Pay save(Pay pay);
    Optional<Pay> findById(long id);
    Pay getById(Long id);
    Optional<Pay> findByPayRequestId(long payRequestId);
    Pay getByPayRequestId(long payRequestId);
}
