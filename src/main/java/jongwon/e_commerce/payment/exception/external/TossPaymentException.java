package jongwon.e_commerce.payment.exception.external;

import jongwon.e_commerce.order.domain.common.exception.InfrastructureException;
import lombok.Getter;

@Getter
public class TossPaymentException extends InfrastructureException {
    public TossPaymentException(String message){
        super(message);
    }
}
