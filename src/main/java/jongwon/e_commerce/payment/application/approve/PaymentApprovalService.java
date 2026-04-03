package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.application.approve.external.PayApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
@Slf4j
public class PaymentApprovalService {
    // 결제 승인 전 주문 정보 검증 부
    private final PaymentService paymentService;
    // 결제 승인 시도 부
    private final PayApprovalExecutor payApprovalExecutor;
    // 결제 후처리 부
    private final List<PayOutcomeHandler> outcomeHandlers;

    public PayApproveOutcomeResponse approvePayment(Member member, PayApproveAttempt attempt){
        //1. 결제 승인 요청 전 주문 정보 검증 후, 결제 생성
        Pay pay = paymentService.preProcess(member, attempt);

        //2. 결제 승인 요청
        PayApproveOutcome outcome = payApprovalExecutor.executePayApprove(attempt);

        //3.handler를 통해, 승인 요청 결과를 결제에 반영
        PayOutcomeHandler payOutcomeHandler = outcomeHandlers.stream().
                filter(h -> h.supports(outcome.getType()))
                .findFirst().get();
        PayApproveOutcomeResponse response = payOutcomeHandler.handle(pay, outcome);

        return response;
    }
}
