package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.dto.PayFailureResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayTimeoutHandler implements PayOutcomeHandler {
    private final PaymentRepository paymentRepository;
    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.TIMEOUT;
    }

    @Override
    @Transactional
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        pay.timeout();
        paymentRepository.save(pay);
        return new PayFailureResponse(
                pay.getPayStatus(),
                "PAYMENT_TIMEOUT",
                "결제 시도가 많습니다. 다시 시도해주세요"
        );
    }
}
