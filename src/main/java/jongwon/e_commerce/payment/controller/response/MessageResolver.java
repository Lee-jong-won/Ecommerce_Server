package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.payment.domain.approve.result.fail.PayErrorCode;

public class MessageResolver {
    public static String resolve(PayErrorCode payErrorCode){
        return switch(payErrorCode){
            case INSUFFICIENT_BALANCE -> "잔액 부족";
            case UNKNOWN_ERROR_CODE -> "등록되지 않은 에러 코드";
            case JSON_PARSING_ERROR -> "JSON 파싱 에러";
            case INVALID_ERROR_RESPONSE -> "잘못된 에러 응답";
            case INVALID_CARD -> "카드 정보가 잘못 되었습니다";
            default -> "알 수 없는 에러입니다";
        };
    }
}
