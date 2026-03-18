package jongwon.e_commerce.payment.repository.jpa.entity;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;

public interface PayDetailEntity {
    void setPayEntity(PayEntity payEntity);
    Pay toModel();
}
