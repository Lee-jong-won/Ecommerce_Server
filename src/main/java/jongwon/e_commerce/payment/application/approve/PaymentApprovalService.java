package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.application.PaymentService;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
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
    private final List<PaymentExecutor> paymentExecutors;
    // 결제 후처리 부
    private final List<PayOutcomeHandler> outcomeHandlers;

    public PayApproveOutcome approvePayment(Member member,
                                            PayApproveAttempt attempt,
                                            String pgType){
        log.info("event = PAYMENT_START " + "paymentKey = {} " + "amount = {} ",
                attempt.getPaymentKey(), attempt.getAmount());

        //1. 결제 승인 요청 전 주문 정보 검증 후, 결제 생성
        Pay pay = paymentService.preProcess(member, attempt);

        //2. 결제 상태 진행중으로 상태 변경 (todo)

        //3. 결제 승인 요청
        PayApproveOutcome outcome = paymentExecutors.stream()
                .filter(h -> h.supports(pgType))
                .findFirst()
                .map(h -> h.executePayApprove(attempt))
                .orElseThrow(() -> {
                    log.error("등록되지 않은 PG사로, 결제 승인 요청이 불가능합니다 : {}", pgType);
                    return new UnsupportedOperationException("지원하지 않는 결제 수단입니다.");
                });

        //4. handler를 통해, 승인 요청 결과를 결제 데이터에 반영
        outcomeHandlers.stream()
                .filter(h -> h.supports(outcome))
                .findFirst()
                .ifPresentOrElse(
                        h -> h.handle(pay, outcome),
                        () -> log.info("No handler for outcome: {}", outcome.getClass().getSimpleName())
                );

        log.info("event = PAYMENT_FINISHED " + "paymentKey = {} " + "amount = {} ",
                attempt.getPaymentKey(), attempt.getAmount());

        return outcome;
    }
}
