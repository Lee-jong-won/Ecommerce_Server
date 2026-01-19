package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;
import jongwon.e_commerce.common.exception.InfrastructureException;

public class TossPaymentApprovalClientFailException extends InfrastructureException {
    public TossPaymentApprovalClientFailException() {
    }
    public TossPaymentApprovalClientFailException(String message) {
        super(message);
    }

}
