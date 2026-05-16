package jongwon.e_commerce.payment.infrastructure.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.infrastructure.jpa.PaymentJpaRepository;
import jongwon.e_commerce.payment.infrastructure.jpa.entity.PayEntity;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    // 결제 공통 정보 저장
    @Override
    @Transactional
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
    public Optional<Pay> findByPayRequestId(long payRequestId) {
        return paymentJpaRepository.findByPayRequestId(payRequestId).map(PayEntity::toModel);
    }

    @Override
    public Pay getByPayRequestId(long payRequestId) {
        return findByPayRequestId(payRequestId).orElseThrow(
                () -> new ResourceNotFoundException("Pay", payRequestId)
        );
    }

}
