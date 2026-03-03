package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;

@Getter
public class PayApproveFail extends PayApproveOutcome {
    public PayApproveFail(){
        super(PayApproveOutcomeType.FAIL);
    }
}
