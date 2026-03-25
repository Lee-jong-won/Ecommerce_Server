package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.PayFailureResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
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
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        pay.failed();
        paymentRepository.save(pay);

        PayApproveFail payApproveFail = (PayApproveFail) outcome;

        return new PayFailureResponse(
                pay.getPayStatus(),
                payApproveFail.getErrorCode(),
                payApproveFail.getMessage()
        );
    }
}
