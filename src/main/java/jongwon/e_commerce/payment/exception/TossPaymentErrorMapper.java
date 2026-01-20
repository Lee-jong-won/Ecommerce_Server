package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentAlreadyProcessedException;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import org.springframework.stereotype.Component;

@Component
public class TossPaymentErrorMapper {
    public TossPaymentException map(String code) {
        return switch (code) {
            // 이미 처리된 결제
            case "ALREADY_PROCESSED_PAYMENT" -> new TossPaymentAlreadyProcessedException(
                    ErrorCode.ALREADY_PROCESSED_PAYMENT
            );

            // 사용자 입력, 비즈니스 오류
            case "INVALID_REQUEST", "NOT_ALLOWED_POINT_USE",
                    "EXCEED_MAX_DAILY_PAYMENT_COUNT", "INVALID_CARD_EXPIRATION",
                    "INVALID_STOPPED_CARD", "INVALID_CARD_NUMBER",
                    "EXCEED_MAX_PAYMENT_AMOUNT", "EXCEED_MAX_AMOUNT",
                    "EXCEED_MAX_AUTH_COUNT", "NOT_AVAILABLE_PAYMENT",
                    "REJECT_CARD_PAYMENT", "FDS_ERROR",
                    "CARD_PROCESSING_ERROR"->
                 new TossPaymentUserFaultException(ErrorCode.valueOf(code));

            // 일시 장애, PG 오류
            case "FAILED_INTERNAL_SYSTEM_PROCESSING", "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                    "PROVIDER_ERROR", "TOO_MANY_REQUESTS", "UNKNOWN_PAYMENT_ERROR" ->
                 new TossPaymentRetryableException(ErrorCode.valueOf(code));

            // 리소스 찾을 수 없음
            case "NOT_FOUND_PAYMENT_SESSION", "NOT_FOUND_PAYMENT" ->
                 new TossPaymentNotFoundException(ErrorCode.valueOf(code));

            // Authorization / security
            case "INVALID_API_KEY", "UNAUTHORIZED_KEY" ->
                    new TossPaymentSystemException(ErrorCode.valueOf(code));

            default -> new TossPaymentException(
                    ErrorCode.UNKNOWN_PAYMENT_ERROR
            );
        };
    }
}
