package jongwon.e_commerce.payment.domain.detail;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.repository.jpa.entity.PayDetailEntity;

public interface PaymentDetail {
    PayMethod getPayMethod();
    Pay getPay();
    void setPay(Pay pay);
    PayDetailEntity toEntity();
}
