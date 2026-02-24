package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;

import java.util.Optional;

public interface PaymentRepository {
    Pay save(Pay pay);
    Optional<Pay> findById(Long id);
    Optional<Pay> findByOrderId(String orderId);
    Optional<Pay> findByFkOrderId(Long fkOrderId);
    void clearStore();
}
