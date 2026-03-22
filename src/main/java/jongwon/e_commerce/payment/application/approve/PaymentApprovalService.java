package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.payment.application.approve.execute.PayApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.preprocess.PayPreprocessor;
import jongwon.e_commerce.payment.application.approve.settlement.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalService {
    // 결제 승인 전 주문 정보 검증 부
    private final PayPreprocessor payPreprocessor; // 추상화 필요
    // 결제 승인 시도 부
    private final PayApprovalExecutor payApprovalExecutor; // 추상화 필요
    private final List<PayOutcomeHandler> outcomeHandlers;

    public PayApproveOutcomeResponse approvePayment(PayApproveAttempt attempt){
        //1. 결제 승인 요청 전 주문 정보 검증 후, 결제 생성
        Pay pay = payPreprocessor.preProcess(attempt);

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
