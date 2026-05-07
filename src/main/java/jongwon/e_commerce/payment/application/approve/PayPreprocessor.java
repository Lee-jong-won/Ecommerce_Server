package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
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
    public PayRequest preProcess(Member member, PayApproveAttempt attempt) {
        log.info("event = PAYMENT_PREPROCESS_START paymentKey = {} amount = {}",
                attempt.getPaymentKey(), attempt.getAmount());

        Order order = orderRepository.getByOrderId(attempt.getOrderName());
        order.validateOwner(member);
        order.validatePayAmount(attempt.getAmount());

        order.paymentPending();
        PayRequest payRequest = PayRequest.from(
                order,
                PayRequest.createOrderId(),
                attempt.getPaymentKey(),
                attempt.getAmount(),
                attempt.getPgType());

        log.info("event = PAYMENT_PREPROCESS_FINISHED paymentKey = {} amount = {}",
                attempt.getPaymentKey(), attempt.getAmount());

        return payRequestRepository.save(payRequest);
    }
}
