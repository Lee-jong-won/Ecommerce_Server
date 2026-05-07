package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InsufficientBalance;

public class StubPaymentApprovalService {

    public PayApproveOutcome returnPayApproveFail(){
        return new InsufficientBalance();
    }
}
