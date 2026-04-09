package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
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
    public void handle(Pay pay, PayApproveOutcome outcome) {
        pay.timeout();
        paymentRepository.save(pay);
    }
}
