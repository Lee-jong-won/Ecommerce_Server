package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.order.domain.common.exception.InfrastructureException;
import lombok.Getter;

@Getter
public class PaymentCompletionConsistencyException extends InfrastructureException {
    private final String orderId;

    public PaymentCompletionConsistencyException(String orderId, Throwable cause) {
        super(cause);
        this.orderId = orderId;
    }
}
