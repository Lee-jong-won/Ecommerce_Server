package jongwon.e_commerce.payment.gateway.dto;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;

public class PayMethodResolver {

    // 토스용 번역기
    public static PayMethod fromToss(String tossValue) {
        return switch (tossValue) {
            case "카드" -> PayMethod.CARD;
            case "계좌이체" -> PayMethod.TRANSFER;
            case "휴대폰" -> PayMethod.MOBILE;
            default -> throw new UnsupportedPayMethodException("토스 미지원 수단: " + tossValue);
        };
    }

    // 나이스용 번역기
    public static PayMethod fromNice(String niceValue) {
        // 나이스는 대소문자가 섞여 올 수 있으므로 처리
        return switch (niceValue.toLowerCase()) {
            case "card" -> PayMethod.CARD;
            case "bank" -> PayMethod.TRANSFER;
            case "cellphone" -> PayMethod.MOBILE;
            default -> throw new UnsupportedPayMethodException("나이스 미지원 수단: " + niceValue);
        };
    }

}
