package jongwon.e_commerce.payment.domain.approve.result.success;

import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import lombok.Getter;

@Getter
public class PayApproveSuccess implements PayApproveOutcome {
    private final PayResult payResult;
    public PayApproveSuccess(PayResult payResult) {
        this.payResult = payResult;
    }
}
