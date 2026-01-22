package jongwon.e_commerce.payment.presentation.dto;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientErrorMessageResolver {
    public String resolve(PaymentErrorCode errorCode) {
        return switch (errorCode.category()) {
            case USER_FAULT -> userFaultMessage(errorCode);
            case REJECT -> rejectMessage();
            case RETRYABLE -> retryableMessage();
            case NOT_FOUND -> notFoundMessage();
            case SECURITY -> securityMessage();
            case ALREADY_PROCESSED -> alreadyProcessedMessage();
            case UNKNOWN -> unknownMessage();
        };
    }

    private String userFaultMessage(PaymentErrorCode code) {
        return switch (code) {
            case INVALID_REQUEST ->
                    "잘못된 결제 정보입니다. 입력한 정보를 확인해 주세요.";
            case EXCEED_MAX_DAILY_PAYMENT_COUNT ->
                    "하루 결제 가능 횟수를 초과했습니다.";
            default ->
                    "결제 정보를 확인해 주세요.";
        };
    }

    private String rejectMessage() {
        return "결제가 거절되었습니다. 다른 결제 수단을 이용해 주세요.";
    }

    private String retryableMessage() {
        return "결제 처리 중 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
    }

    private String notFoundMessage() {
        return "결제 정보를 찾을 수 없습니다. 새로 결제를 진행해 주세요.";
    }

    private String securityMessage() {
        return "결제 요청을 처리할 수 없습니다. 관리자에게 문의해 주세요.";
    }

    private String alreadyProcessedMessage() {
        return "이미 처리된 결제입니다.";
    }

    private String unknownMessage() {
        return "결제 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
    }
}
