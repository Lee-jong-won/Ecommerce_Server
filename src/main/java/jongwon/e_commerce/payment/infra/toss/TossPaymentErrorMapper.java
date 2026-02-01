package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.TossPaymentNotFoundException;
import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentAlreadyProcessedException;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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
        } catch (NullPointerException e) {
            log.error("[TOSS_API_SPEC_CHANGE]", e);
            return PaymentErrorCode.UNKNOWN_ERROR;
        } catch(IllegalArgumentException e){
            log.error("[TOSS_UNKNOWN_ERROR_CODE]", e);
            return PaymentErrorCode.UNKNOWN_ERROR;
        }
    }
}
