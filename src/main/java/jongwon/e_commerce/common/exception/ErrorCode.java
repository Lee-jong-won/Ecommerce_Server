package jongwon.e_commerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 1.결제 승인 관련 에러 코드들
    // ===== 400 BAD_REQUEST =====
    ALREADY_PROCESSED_PAYMENT(
            HttpStatus.BAD_REQUEST.value(),
            "ALREADY_PROCESSED_PAYMENT",
            "이미 처리된 결제 입니다."
    ,true),

    PROVIDER_ERROR(
            HttpStatus.BAD_REQUEST.value(),
            "PROVIDER_ERROR",
            "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
    ,true),

    EXCEED_MAX_CARD_INSTALLMENT_PLAN(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_CARD_INSTALLMENT_PLAN",
            "설정 가능한 최대 할부 개월 수를 초과했습니다."
    ,true),

    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_REQUEST",
            "잘못된 결제 정보입니다. 결제 정보를 확인하고, 다시 결제해주세요"
    ,true),

    NOT_ALLOWED_POINT_USE(
            HttpStatus.BAD_REQUEST.value(),
            "NOT_ALLOWED_POINT_USE",
            "포인트 사용이 불가한 카드입니다."
    ,true),

    INVALID_API_KEY(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_API_KEY",
            "잘못된 시크릿키 연동 정보 입니다."
    ,false),

    BELOW_MINIMUM_AMOUNT(
            HttpStatus.BAD_REQUEST.value(),
            "BELOW_MINIMUM_AMOUNT",
            "최소 결제 금액 조건을 만족하지 않습니다."
    ,true),

    INVALID_CARD_EXPIRATION(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_CARD_EXPIRATION",
            "카드 정보를 다시 확인해주세요. (유효기간)"
    ,true),

    INVALID_STOPPED_CARD(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_STOPPED_CARD",
            "정지된 카드 입니다."
    , true),

    EXCEED_MAX_DAILY_PAYMENT_COUNT(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_DAILY_PAYMENT_COUNT",
            "하루 결제 가능 횟수를 초과했습니다."
    ,true),

    INVALID_CARD_NUMBER(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_CARD_NUMBER",
            "카드번호를 다시 확인해주세요."
    ,true),

    EXCEED_MAX_PAYMENT_AMOUNT(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_PAYMENT_AMOUNT",
            "하루 결제 가능 금액을 초과했습니다."
    ,true),

    CARD_PROCESSING_ERROR(
            HttpStatus.BAD_REQUEST.value(),
            "CARD_PROCESSING_ERROR",
            "카드사에서 오류가 발생했습니다."
    , true),

    EXCEED_MAX_AMOUNT(
            HttpStatus.BAD_REQUEST.value(),
            "EXCEED_MAX_AMOUNT",
            "거래금액 한도를 초과했습니다."
    , true),

    NOT_AVAILABLE_PAYMENT(
            HttpStatus.BAD_REQUEST.value(),
            "NOT_AVAILABLE_PAYMENT",
            "결제가 불가능한 시간대입니다."
    , true),

    // ===== 401 =====

    UNAUTHORIZED_KEY(
            HttpStatus.UNAUTHORIZED.value(),
            "UNAUTHORIZED_KEY",
            "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
    , false),

    // ===== 403 =====

    REJECT_CARD_PAYMENT(
            HttpStatus.FORBIDDEN.value(),
            "REJECT_CARD_PAYMENT",
            "한도초과 혹은 잔액부족으로 결제에 실패했습니다."
    , true),

    REJECT_ACCOUNT_PAYMENT(
            HttpStatus.FORBIDDEN.value(),
            "REJECT_ACCOUNT_PAYMENT",
            "잔액부족으로 결제에 실패했습니다."
    , true),

    FORBIDDEN_REQUEST(
            HttpStatus.FORBIDDEN.value(),
            "FORBIDDEN_REQUEST",
            "잘못된 결제 정보 입니다. 결제 정보를 확인하고, 다시 결제해주세요."
    , true),

    EXCEED_MAX_AUTH_COUNT(
            HttpStatus.FORBIDDEN.value(),
            "EXCEED_MAX_AUTH_COUNT",
            "최대 인증 횟수를 초과했습니다."
    , true),

    INVALID_PASSWORD(
            HttpStatus.FORBIDDEN.value(),
            "INVALID_PASSWORD",
            "결제 비밀번호가 일치하지 않습니다."
    , true),

    FDS_ERROR(
            HttpStatus.FORBIDDEN.value(),
            "FDS_ERROR",
            "위험거래가 감지되어 결제가 제한됩니다."
                    ,true ),

    // ===== 404 =====
    NOT_FOUND_PAYMENT(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND_PAYMENT",
            "존재하지 않는 결제 정보 입니다."
    ,true),

    NOT_FOUND_PAYMENT_SESSION(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND_PAYMENT_SESSION",
            "결제 시간이 만료되었습니다."
    ,true),

    // ===== 500 =====
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            "결제가 완료되지 않았습니다. 다시 시도해주세요."
    ,true),

    FAILED_INTERNAL_SYSTEM_PROCESSING(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "FAILED_INTERNAL_SYSTEM_PROCESSING",
            "내부 시스템 처리 작업이 실패했습니다."
    ,true),

    UNKNOWN_PAYMENT_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "UNKNOWN_PAYMENT_ERROR",
            "결제에 실패했습니다. 같은 문제가 반복된다면 카드사로 문의해주세요."
    ,true),

    PG_TOO_MANY_REQUEST(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            "PG_TOO_MANY_REQUEST",
            "결제 요청이 너무 많습니다. 잠시 후 다시 시도 해주세요."
            ,true),

    // ===== 우리 서버 공통 =====
    TOSS_API_TIMEOUT_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "TOSS_API_NETWORK_ERROR",
            "결제 요청이 너무 많습니다. 잠시 후 다시 시도해주세요!"
    ,true),

    TOSS_API_NETWORK_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "TOSS_API_NETWORK_ERROR",
            "네트워크 오류입니다. 다시 한번 시도해주세요!"
            ,true),

    PG_RESPONSE_PARSE_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "PG_RESPONSE_PARSE_ERROR",
            "PG사 응답 파싱 중 오류가 발생 했습니다."
    ,false),

    INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "결제 중 오류가 발생 했습니다. 다시 한번 시도해주세요!"
    ,true);

    private final int status;
    private final String code;
    private final String description;
    private final boolean clientExposed;

    ErrorCode(int status, String code, String description, boolean clientExposed){
        this.status = status;
        this.code = code;
        this.description = description;
        this.clientExposed = clientExposed;
    }
}
