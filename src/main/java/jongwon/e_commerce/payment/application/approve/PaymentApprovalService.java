package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.application.approve.execute.PaymentApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.settlement.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.application.approve.validate.OrderValidator;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalService {
    // 결제 승인 전 주문 정보 검증 부
    private final OrderValidator orderValidator;
    // 결제 승인 시도 부
    private final PaymentApprovalExecutor paymentApprovalExecutor;
    private final List<PayOutcomeHandler> outcomeHandlers;

    public PayApproveOutcomeResponse approvePayment(PayApproveAttempt attempt){
        String orderId = attempt.getOrderId();
        String paymentKey = attempt.getPaymentKey();
        long amount = attempt.getAmount();

        //1. 결제 승인 요청 전 주문 정보 검증 후, 결제 생성
        Order order = orderValidator.validateForPayment(orderId, amount);
        Pay pay = Pay.from(order, paymentKey, amount);

        //2. 결제 승인 요청
        PayApproveOutcome outcome = paymentApprovalExecutor.execute(attempt, UUID.randomUUID().toString());

        //3.handler를 통해, 승인 요청 결과를 결제에 반영
        PayOutcomeHandler payOutcomeHandler = outcomeHandlers.stream().
                filter(h -> h.supports(outcome.getType()))
                .findFirst().orElseThrow();

        PayApproveOutcomeResponse response = payOutcomeHandler.handle(pay, outcome);
        return response;
    }
}
