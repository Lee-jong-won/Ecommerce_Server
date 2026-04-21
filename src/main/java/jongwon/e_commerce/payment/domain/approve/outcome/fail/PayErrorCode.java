package jongwon.e_commerce.payment.domain.approve.outcome.fail;

public enum PayErrorCode {
    INSUFFICIENT_BALANCE,
    INVALID_CARD,
    INVALID_ERROR_RESPONSE,
    JSON_PARSING_ERROR,
    UNKNOWN_ERROR_CODE;
}
