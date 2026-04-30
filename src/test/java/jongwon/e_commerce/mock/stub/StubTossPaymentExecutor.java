package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;

public class StubTossPaymentExecutor implements PaymentExecutor {

    @Override
    public boolean supports(PGType pgType) {
        return PGType.TOSS == pgType;
    }

    @Override
    public PayApproveOutcome executePayApprove(PayApproveAttempt request) {
        return new PayApproveSuccess(PayResult.builder().build());
    }
}
