package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;

public class StubTossPaymentExecutor implements PaymentExecutor {

    @Override
    public boolean supports(String providerName) {
        return "TOSS".equals(providerName);
    }

    @Override
    public PayApproveOutcome executePayApprove(PayApproveAttempt request) {
        return new PayApproveSuccess(PayResult.builder().build());
    }
}
