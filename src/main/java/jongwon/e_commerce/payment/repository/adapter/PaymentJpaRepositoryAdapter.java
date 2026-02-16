package jongwon.e_commerce.payment.repository.adapter;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class PaymentJpaRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Pay save(Long fkOrderId, String orderId, String pgType, Long payAmount) {
        Pay pay = Pay.create(fkOrderId, orderId, pgType, payAmount);
        return paymentJpaRepository.save(pay);
    }

    @Override
    public Pay findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException()
        );
    }

    @Override
    public Pay findByFkOrderId(Long fkOrderId) {
        return paymentJpaRepository.findByFkOrderId(fkOrderId).orElseThrow(
                () -> new PaymentNotFoundException()
        );
    }
}
