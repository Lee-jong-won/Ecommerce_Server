package jongwon.e_commerce.payment.infrastructure.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.infrastructure.jpa.PayRequestJpaRepository;
import jongwon.e_commerce.payment.infrastructure.jpa.entity.PayRequestEntity;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class PayRequestRepositoryImpl implements PayRequestRepository {

    private final PayRequestJpaRepository payRequestJpaRepository;

    @Override
    public PayRequest save(PayRequest payRequest) {
        return payRequestJpaRepository.save(PayRequestEntity.from(payRequest)).toModel();
    }

    @Override
    public Optional<PayRequest> findById(long id) {
        return payRequestJpaRepository.findById(id).map(PayRequestEntity::toModel);
    }

    @Override
    public PayRequest getById(Long id) {
        return findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pay Request", id)
        );
    }

    @Override
    public Optional<PayRequest> findByPaymentKey(String paymentKey) {
        return payRequestJpaRepository.findByPaymentKey(paymentKey).map(PayRequestEntity::toModel);
    }

    @Override
    public PayRequest getByPaymentKey(String paymentKey) {
        return findByPaymentKey(paymentKey).orElseThrow(
                () -> new ResourceNotFoundException("Pay Request", paymentKey)
        );
    }
}
