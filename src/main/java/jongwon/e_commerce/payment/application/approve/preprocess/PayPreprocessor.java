package jongwon.e_commerce.payment.application.approve.preprocess;

import jongwon.e_commerce.order.application.OrderValidator;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayPreprocessor {

    private final OrderValidator orderValidator;
    private final PaymentRepository paymentRepository;

    public Pay preProcess(PayApproveAttempt attempt){
        String orderId = attempt.getOrderId();
        String paymentKey = attempt.getPaymentKey();
        long amount = attempt.getAmount();

        Order order = orderValidator.validateForPayment(orderId, amount);
        Pay pay = Pay.from(order, paymentKey, amount);
        paymentRepository.save(pay);
        return pay;
    }

}
