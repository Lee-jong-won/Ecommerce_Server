package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.result.unknown.PayApproveTimeout;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayTimeoutHandler implements PayOutcomeHandler {
    private final PaymentRepository paymentRepository;
    @Override
    public boolean supports(PayApproveOutcome outcome) {
        return outcome instanceof PayApproveTimeout;
    }

    @Override
    @Transactional
    public void handle(Pay pay, PayApproveOutcome outcome) {
        pay.timeout();
        paymentRepository.save(pay);
    }
}
