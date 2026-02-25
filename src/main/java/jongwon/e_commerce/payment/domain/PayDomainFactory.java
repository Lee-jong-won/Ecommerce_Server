package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.springframework.stereotype.Service;

public abstract class PayDomainFactory {
    public static Pay from(Order order, TossPaymentApproveResponse response){
        String method = response.getMethod();
        Pay pay;

        switch(method) {
            case "휴대폰" -> pay = MPPay.create(order, response);
            default -> throw new UnsupportedPayMethodException("지원하지 않은 결제 타입입니다");
        }

        return pay;
    }
}
