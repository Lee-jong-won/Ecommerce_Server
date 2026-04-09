package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayApproveTimeout extends PayApproveOutcome {
    public PayApproveTimeout(){
        super(PayApproveOutcomeType.TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
    }
}
