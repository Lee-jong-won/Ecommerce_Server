package jongwon.e_commerce.payment.application.approve.settlement.handler;

import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayTimeoutHandler implements PayOutcomeHandler {
    private final PaymentRepository paymentRepository;
    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.TIMEOUT;
    }

    @Override
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        pay.timeout();
        paymentRepository.save(pay);
        return new PayApproveOutcomeResponse(
                PayStatus.TIME_OUT,
                "PAYMENT_TIMEOUT",
                "결제 시도가 많습니다. 다시 시도해주세요"
        );
    }
}
