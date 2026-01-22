package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentAlreadyProcessedException;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import org.springframework.stereotype.Component;

@Component
public class TossPaymentErrorMapper {
    public TossPaymentException map(String code) {

        PaymentErrorCode paymentErrorCode = parse(code);
        return switch (paymentErrorCode.category()) {
            case USER_FAULT, REJECT ->
                    new TossPaymentUserFaultException(paymentErrorCode);
            case RETRYABLE ->
                    new TossPaymentRetryableException(paymentErrorCode);
            case NOT_FOUND ->
                    new TossPaymentNotFoundException(paymentErrorCode);
            case SECURITY ->
                    new TossPaymentSystemException(paymentErrorCode);
            case ALREADY_PROCESSED ->
                    new TossPaymentAlreadyProcessedException(paymentErrorCode);
            case UNKNOWN ->
                    new TossPaymentException(PaymentErrorCode.UNKNOWN_ERROR);
        };
    }

    private PaymentErrorCode parse(String code) {
        try {
            return PaymentErrorCode.valueOf(code);
        } catch (IllegalArgumentException e) {
            return PaymentErrorCode.UNKNOWN_ERROR;
        }
    }
}
