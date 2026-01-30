package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.infra.PaymentRepository;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PaymentPrepareService {

    private final OrderValidator orderValidator;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void preparePayment(TossPaymentApproveRequest request){
        // 1. 주문 검증
        Order order = orderValidator.validateOrder(request);

        // 2. Payment 생성
        Pay payment = Pay.create(
                request.getOrderId(),
                request.getPayOrderId(),
                request.getOrderName(),
                request.getPaymentKey(),
                request.getAmount()
        );
        paymentRepository.save(payment);
    }
}
