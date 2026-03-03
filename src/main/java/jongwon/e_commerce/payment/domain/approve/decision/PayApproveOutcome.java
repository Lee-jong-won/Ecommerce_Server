package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;

@Getter
public abstract class PayApproveOutcome {
    private final PayApproveOutcomeType type;
    protected PayApproveOutcome(PayApproveOutcomeType type) {
        this.type = type;
    }
}
