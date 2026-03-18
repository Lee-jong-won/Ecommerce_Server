package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.jpa.MPPayJpaRepository;
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
public class PaymentJpaRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Pay save(Pay pay) {

        paymentJpaRepository.save(PayEntity.from(pay));

    }

    @Override
    public Pay saveDetail(Pay pay) {
        PayEntity payEntity = paymentJpaRepository.

        PaymentDetail paymentDetail = pay.getPaymentDetail();
        PayDetailEntity payDetailEntity = paymentDetail.toEntity();
        payDetailEntity.setPayEntity(payEntity);
        paymentJpaRepository.save(payEntity);

        return payDetailEntity.toModel();
    }

    @Override
    public Optional<PayEntity> findById(long id) {
        return Optional.empty();
    }

}
