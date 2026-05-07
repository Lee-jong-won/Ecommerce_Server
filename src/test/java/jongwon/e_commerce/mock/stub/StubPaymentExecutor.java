package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.exception.PayApproveException;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StubPaymentExecutor implements PaymentExecutor {

    private final PayResult payResult;
    private final PayApproveException payApproveException;

    public static StubPaymentExecutor success(PayResult payResult){
        return new StubPaymentExecutor(payResult, null);
    }

    public static StubPaymentExecutor failure(PayApproveException payApproveException){
        return new StubPaymentExecutor(null, payApproveException);
    }

    @Override
    public boolean supports(PGType pgType) {
        return true;
    }

    @Override
    public PayResult executePayApprove(PayApproveAttempt request) {
        if(payApproveException != null) throw payApproveException;
        return payResult;
    }
}
