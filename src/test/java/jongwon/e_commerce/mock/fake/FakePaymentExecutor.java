package jongwon.e_commerce.mock.fake;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;

public class FakePaymentExecutor implements PaymentExecutor {
    private final PayApproveOutcome outcome;

    public FakePaymentExecutor(PayApproveOutcome outcome) {
        this.outcome = outcome;
    }

    @Override
    public boolean supports(PGType pgType) {
        return true;
    }

    @Override
    public PayApproveOutcome executePayApprove(PayApproveAttempt request) {
        return outcome;
    }
}
