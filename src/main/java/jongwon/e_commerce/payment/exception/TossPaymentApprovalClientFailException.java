package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class TossPaymentApprovalClientFailException extends DomainException {
    public TossPaymentApprovalClientFailException() {
    }
    public TossPaymentApprovalClientFailException(String message) {
        super(message);
    }

}
