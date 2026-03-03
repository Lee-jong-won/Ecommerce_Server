package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.repository.jpa.entity.PayEntity;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class PaymentJpaRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    @Override
    public void save(Pay pay) {
        paymentJpaRepository.save(PayEntity.from(pay));
    }

}
