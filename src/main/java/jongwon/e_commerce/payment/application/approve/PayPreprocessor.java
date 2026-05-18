package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayPreprocessor {

    private final OrderRepository orderRepository;
    private final PayRequestRepository payRequestRepository;

    @Transactional
    public PayRequest preProcess(int expectedAmount, PayApproveAttempt attempt) {
        Order order = orderRepository.getByOrderId(attempt.getOrderId());
        PayRequest payRequest = mapFrom(order.getId(), attempt);

        if(order.getTotalAmount() == payRequest.getPayAmount()
                && payRequest.getPayAmount() == expectedAmount)
            throw new InvalidAmountException();

        order.paymentPending();
        orderRepository.save(order);

        return payRequestRepository.save(payRequest);
    }

    private PayRequest mapFrom(Long orderId, PayApproveAttempt payApproveAttempt){
        return PayRequest.from(orderId,
                payApproveAttempt.getPaymentKey(),
                payApproveAttempt.getAmount(),
                PGType.from(payApproveAttempt.getPgType()));
    }
}
