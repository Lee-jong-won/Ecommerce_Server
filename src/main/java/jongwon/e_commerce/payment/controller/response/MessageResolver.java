package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.payment.exception.PayErrorCode;

public class MessageResolver {
    public static String resolve(PayErrorCode payErrorCode) {
        return switch (payErrorCode) {
            case INSUFFICIENT_BALANCE -> "잔액 부족";
            case INVALID_CARD -> "카드 정보가 잘못 되었습니다";
        };
    }
}
