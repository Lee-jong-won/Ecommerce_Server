package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public Pay getById(long id){
        return paymentRepository.getById(id);
    }

    @Transactional
    public Pay preProcess(PayApproveAttempt attempt){
        String orderId = attempt.getOrderId();;
        String paymentKey = attempt.getPaymentKey();;
        long amount = attempt.getAmount();

        Order order = orderRepository.getByOrderId(orderId);
        if(order.getTotalAmount() != amount)
            throw new InvalidAmountException();

        Pay pay = Pay.from(order, paymentKey, amount);
        return paymentRepository.save(pay);
    }

    @Transactional
    public Pay update(long id, PayResult.PayResultCommon payResultCommon){
        Pay pay = getById(id);
        pay = pay.reflectPaySuccess(payResultCommon);
        return paymentRepository.save(pay);
    }

}
