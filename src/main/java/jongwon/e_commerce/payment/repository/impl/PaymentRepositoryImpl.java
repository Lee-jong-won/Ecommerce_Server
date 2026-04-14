package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.jpa.entity.PayDetailEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    // 결제 공통 정보 저장
    @Override
    public Pay save(Pay pay) {
        return paymentJpaRepository.save(PayEntity.from(pay)).toModel();
    }

    @Override
    public Optional<Pay> findById(long id) {
        return paymentJpaRepository.findById(id).map(PayEntity::toModel);
    }

    @Override
    public Pay getById(Long id) {
        return findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pay", id)
        );
    }

    @Override
    public Optional<Pay> findByPaymentKey(String paymentKey) {
        return paymentJpaRepository.findByPaymentKey(paymentKey).map(PayEntity::toModel);
    }

    @Override
    public Pay getByPaymentKey(String paymentKey) {
        return findByPaymentKey(paymentKey).orElseThrow(
                () -> new ResourceNotFoundException("Pay", paymentKey)
        );
    }

}
