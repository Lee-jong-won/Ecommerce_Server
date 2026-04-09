package jongwon.e_commerce.payment.domain.approve.decision;

import jongwon.e_commerce.payment.domain.approve.PayResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayApproveSuccess extends PayApproveOutcome {
    private final PayResult payResult;
    public PayApproveSuccess(PayResult payResult) {
        super(PayApproveOutcomeType.SUCCESS, HttpStatus.CREATED);
        this.payResult = payResult;
    }
}
