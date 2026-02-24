package jongwon.e_commerce.payment.application.approve.result.context;

import jongwon.e_commerce.payment.application.approve.result.context.PaymentDetail;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;

public interface PaymentDetailCreator {
    PayMethod supports();
    void createDetailOf(Pay pay, PaymentDetail detail);
}
