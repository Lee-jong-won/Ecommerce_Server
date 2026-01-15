package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;
import jongwon.e_commerce.common.exception.InfrastructureException;

public class TossPaymentApprovalPGFailException extends InfrastructureException {
    public TossPaymentApprovalPGFailException() {
    }
    public TossPaymentApprovalPGFailException(String message) {
        super(message);
    }

}
