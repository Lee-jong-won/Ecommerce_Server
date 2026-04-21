package jongwon.e_commerce.payment.domain.approve.outcome.success;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import lombok.Getter;

@Getter
public class PayApproveSuccess implements PayApproveOutcome {
    private final PayResult payResult;
    public PayApproveSuccess(PayResult payResult) {
        this.payResult = payResult;
    }
}
