package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;

@Getter
public class PayApproveTimeout extends PayApproveOutcome {
    public PayApproveTimeout(){
        super(PayApproveOutcomeType.TIMEOUT);
    }
}
