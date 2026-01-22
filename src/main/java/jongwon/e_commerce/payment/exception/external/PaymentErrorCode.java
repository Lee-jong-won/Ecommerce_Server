package jongwon.e_commerce.payment.exception.external;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {
    /* =========================
     * Already processed
     * ========================= */
    ALREADY_PROCESSED_PAYMENT(
            HttpStatus.BAD_REQUEST.value(),
            "ALREADY_PROCESSED_PAYMENT",
            ErrorCategory.ALREADY_PROCESSED
    ),

    /* =========================
     * Security / Authorization
     * ========================= */
    INVALID_API_KEY(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_API_KEY",
            ErrorCategory.SECURITY
    ),
    UNAUTHORIZED_KEY(
            HttpStatus.UNAUTHORIZED.value(),
            "UNAUTHORIZED_KEY",
            ErrorCategory.SECURITY
    ),

    /* =========================
     * User fault (입력/비즈니스)
     * ========================= */
    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_REQUEST",
            ErrorCategory.USER_FAULT
    ),
    NOT_ALLOWED_POINT_USE(
            HttpStatus.BAD_REQUEST.value(),
            "NOT_ALLOWED_POINT_USE",
            ErrorCategory.USER_FAULT
    ),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_CARD_INSTALLMENT_PLAN",
            ErrorCategory.USER_FAULT
    ),
    BELOW_MINIMUM_AMOUNT(
            HttpStatus.BAD_REQUEST.value(),
            "BELOW_MINIMUM_AMOUNT",
            ErrorCategory.USER_FAULT
    ),
    INVALID_CARD_EXPIRATION(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_CARD_EXPIRATION",
            ErrorCategory.USER_FAULT
    ),
    INVALID_STOPPED_CARD(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_STOPPED_CARD",
            ErrorCategory.USER_FAULT
    ),
    EXCEED_MAX_DAILY_PAYMENT_COUNT(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_DAILY_PAYMENT_COUNT",
            ErrorCategory.USER_FAULT
    ),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(
            HttpStatus.BAD_REQUEST.value(),
            "NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT",
            ErrorCategory.USER_FAULT
    ),
    INVALID_ACCOUNT_INFO_RE_REGISTER(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_ACCOUNT_INFO_RE_REGISTER",
            ErrorCategory.USER_FAULT
    ),
    NOT_AVAILABLE_PAYMENT(
            HttpStatus.BAD_REQUEST.value(),
            "NOT_AVAILABLE_PAYMENT",
            ErrorCategory.USER_FAULT
    ),
    UNAPPROVED_ORDER_ID(
            HttpStatus.BAD_REQUEST.value(),
            "UNAPPROVED_ORDER_ID",
            ErrorCategory.USER_FAULT
    ),
    EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT",
            ErrorCategory.USER_FAULT
    ),
    INVALID_PASSWORD(
            HttpStatus.FORBIDDEN.value(),
            "INVALID_PASSWORD",
            ErrorCategory.USER_FAULT
    ),
    INCORRECT_BASIC_AUTH_FORMAT(
            HttpStatus.FORBIDDEN.value(),
            "INCORRECT_BASIC_AUTH_FORMAT",
            ErrorCategory.USER_FAULT
    ),

    /* =========================
     * Reject (카드사 / 은행 / FDS)
     * ========================= */
    REJECT_ACCOUNT_PAYMENT(
            HttpStatus.FORBIDDEN.value(),
            "REJECT_ACCOUNT_PAYMENT",
            ErrorCategory.REJECT
    ),
    REJECT_CARD_PAYMENT(
            HttpStatus.FORBIDDEN.value(),
            "REJECT_CARD_PAYMENT",
            ErrorCategory.REJECT
    ),
    REJECT_CARD_COMPANY(
            HttpStatus.FORBIDDEN.value(),
            "REJECT_CARD_COMPANY",
            ErrorCategory.REJECT
    ),
    FORBIDDEN_REQUEST(
            HttpStatus.FORBIDDEN.value(),
            "FORBIDDEN_REQUEST",
            ErrorCategory.REJECT
    ),
    EXCEED_MAX_AUTH_COUNT(
            HttpStatus.FORBIDDEN.value(),
            "EXCEED_MAX_AUTH_COUNT",
            ErrorCategory.REJECT
    ),
    EXCEED_MAX_ONE_DAY_AMOUNT(
            HttpStatus.FORBIDDEN.value(),
            "EXCEED_MAX_ONE_DAY_AMOUNT",
            ErrorCategory.REJECT
    ),
    NOT_AVAILABLE_BANK(
            HttpStatus.FORBIDDEN.value(),
            "NOT_AVAILABLE_BANK",
            ErrorCategory.REJECT
    ),
    FDS_ERROR(
            HttpStatus.FORBIDDEN.value(),
            "FDS_ERROR",
            ErrorCategory.REJECT
    ),

    /* =========================
     * Not found
     * ========================= */
    NOT_FOUND_PAYMENT(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND_PAYMENT",
            ErrorCategory.NOT_FOUND
    ),
    NOT_FOUND_PAYMENT_SESSION(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND_PAYMENT_SESSION",
            ErrorCategory.NOT_FOUND
    ),

    /* =========================
     * Retryable / Internal error
     * ========================= */
    PROVIDER_ERROR(
            HttpStatus.BAD_REQUEST.value(),
            "PROVIDER_ERROR",
            ErrorCategory.RETRYABLE
    ),
    FAILED_INTERNAL_SYSTEM_PROCESSING(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "FAILED_INTERNAL_SYSTEM_PROCESSING",
            ErrorCategory.RETRYABLE
    ),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            ErrorCategory.RETRYABLE
    ),
    UNKNOWN_PAYMENT_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "UNKNOWN_PAYMENT_ERROR",
            ErrorCategory.RETRYABLE
    ),
    TOSS_API_TIMEOUT_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "TOSS_API_NETWORK_ERROR",
                   ErrorCategory.RETRYABLE
    ),
    TOSS_API_NETWORK_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "TOSS_API_NETWORK_ERROR",
                    ErrorCategory.RETRYABLE
    ),

    /* =========================
     * UNKNOWN
     * ========================= */
    UNKNOWN_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "UNKNOWN_ERROR",
            ErrorCategory.UNKNOWN
    );

    private final int status;
    private final String code;
    private final ErrorCategory category;

    PaymentErrorCode(int status, String code, ErrorCategory category) {
        this.status = status;
        this.code = code;
        this.category = category;
    }

    public ErrorCategory category() {
        return this.category;
    }
}
