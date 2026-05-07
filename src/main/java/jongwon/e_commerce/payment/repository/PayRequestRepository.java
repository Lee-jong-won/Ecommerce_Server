package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayRequest;

import java.util.Optional;

public interface PayRequestRepository {
    PayRequest save(PayRequest payRequest);
    Optional<PayRequest> findById(long id);
    PayRequest getById(Long id);
    Optional<PayRequest> findByPaymentKey(String paymentKey);
    PayRequest getByPaymentKey(String paymentKey);
}
