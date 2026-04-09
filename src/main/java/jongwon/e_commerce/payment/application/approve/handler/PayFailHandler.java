package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayFailHandler implements PayOutcomeHandler {

    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.FAIL;
    }

    @Override
    @Transactional
    public void handle(Pay pay, PayApproveOutcome outcome) {
        PayApproveFail payApproveFail = (PayApproveFail) outcome;
        if(payApproveFail.getPayErrorCode().shouldPayFail())
            pay.failed();
        paymentRepository.save(pay);
    }
}
