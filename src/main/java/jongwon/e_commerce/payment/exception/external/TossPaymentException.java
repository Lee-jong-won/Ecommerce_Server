package jongwon.e_commerce.payment.exception.external;

import jongwon.e_commerce.common.exception.InfrastructureException;
import lombok.Getter;

@Getter
public class TossPaymentException extends InfrastructureException {
    private final PaymentErrorCode errorCode;

    public TossPaymentException(PaymentErrorCode errorCode){
        this.errorCode = errorCode;
    }

}
