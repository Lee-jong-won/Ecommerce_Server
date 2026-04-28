package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import java.util.Arrays;

public enum PayMethod {
    CARD("카드"),               // 카드
    TRANSFER("계좌이체"),           // 계좌이체
    VIRTUAL_ACCOUNT("가상계좌"),    // 가상계좌
    MOBILE("휴대폰"),             // 휴대폰 결제
    EASY_PAY("간편결제");           // 간편결제

    private final String viewValue;

    PayMethod(String viewValue){
        this.viewValue = viewValue;
    }

}
