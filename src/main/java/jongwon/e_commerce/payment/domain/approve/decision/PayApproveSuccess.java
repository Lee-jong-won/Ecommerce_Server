package jongwon.e_commerce.payment.domain.approve.decision;

import jongwon.e_commerce.payment.domain.approve.PayResult;
import lombok.Getter;

@Getter
public class PayApproveSuccess extends PayApproveOutcome {
    private final PayResult payResult;
    public PayApproveSuccess(PayResult payResult) {
        super(PayApproveOutcomeType.SUCCESS);
        this.payResult = payResult;
    }
}
