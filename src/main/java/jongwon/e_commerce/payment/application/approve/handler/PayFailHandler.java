package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.result.fail.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayFailHandler implements PayOutcomeHandler {

    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PayApproveOutcome outcome) {
        return outcome instanceof PayApproveFail;
    }

    @Override
    @Transactional
    public void handle(Pay pay, PayApproveOutcome outcome) {
        pay.failed();
        paymentRepository.save(pay);
    }
}
