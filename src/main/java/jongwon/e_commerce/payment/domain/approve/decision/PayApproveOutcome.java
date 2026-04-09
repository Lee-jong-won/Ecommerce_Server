package jongwon.e_commerce.payment.domain.approve.decision;

import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class PayApproveOutcome {
    private final PayApproveOutcomeType type;
    protected PayApproveOutcome(PayApproveOutcomeType type) {
        this.type = type;
    }
    abstract PayApproveOutcomeResponse toPayApproveOutcomeResponse(HttpStatus httpStatus);
}
