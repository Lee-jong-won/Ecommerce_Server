package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class PayApproveOutcome {
    private final PayApproveOutcomeType type;
    private final HttpStatus httpStatus;
    protected PayApproveOutcome(PayApproveOutcomeType type, HttpStatus httpStatus) {
        this.type = type;
        this.httpStatus = httpStatus;
    }
}
