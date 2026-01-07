package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;

import java.util.Arrays;

public enum PayMethodMapper {
    CARD("카드", PayMethod.CARD),
    TRANSFER("계좌이체", PayMethod.TRANSFER),
    VIRTUAL_ACCOUNT("가상계좌", PayMethod.VIRTUAL_ACCOUNT),
    MOBILE("휴대폰", PayMethod.MOBILE),
    EASY_PAY("간편결제", PayMethod.EASY_PAY),

    // 상품권 계열은 하나로 통합
    CULTURE_GIFT("문화상품권", PayMethod.GIFT_CERTIFICATE),
    BOOK_GIFT("도서문화상품권", PayMethod.GIFT_CERTIFICATE),
    GAME_GIFT("게임문화상품권", PayMethod.GIFT_CERTIFICATE);

    private final String tossValue;
    private final PayMethod payMethod;

    PayMethodMapper(String tossValue, PayMethod payMethod) {
        this.tossValue = tossValue;
        this.payMethod = payMethod;
    }

    public static PayMethod from(String tossValue) {
        return Arrays.stream(values())
                .filter(m -> m.tossValue.equals(tossValue))
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedPayMethodException("지원하지 않는 결제 수단: " + tossValue)
                )
                .payMethod;
    }
}
