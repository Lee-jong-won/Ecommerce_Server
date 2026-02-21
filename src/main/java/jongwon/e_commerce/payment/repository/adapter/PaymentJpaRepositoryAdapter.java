package jongwon.e_commerce.payment.repository.adapter;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class PaymentJpaRepositoryAdapter implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    @Override
    public Pay save(Long fkOrderId, String paymentId, String orderId, Long payAmount) {
        Pay pay = Pay.create(fkOrderId, paymentId, orderId, payAmount);
        return paymentJpaRepository.save(pay);
    }

    @Override
    public Optional<Pay> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    @Override
    public Optional<Pay> findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }

    @Override
    public Optional<Pay> findByFkOrderId(Long fkOrderId) {
        return paymentJpaRepository.findByFkOrderId(fkOrderId);
    }

    @Override
    public void clearStore() {
        paymentJpaRepository.deleteAll();
    }
}
