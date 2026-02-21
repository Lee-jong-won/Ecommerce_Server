package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PreparePaymentApprovalService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Pay preparePaymentApproval(String paymentId, String orderId, long amount) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException()
        );

        //가격이 일치 하지 않으면, 예외 throw
        if (order.getTotalAmount() != amount)
            throw new InvalidAmountException();

        //일치 하면, Payment 객체 생성, order 상태 변경
        Pay pay = paymentRepository.save(
                order.getId(),
                paymentId,
                order.getOrderId(),
                order.getTotalAmount());
        order.markPaymentPending();

        return pay;
    }
}
