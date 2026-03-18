package jongwon.e_commerce.payment.domain.detail;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.repository.jpa.entity.PayDetailEntity;

public interface PaymentDetail {
    PayMethod getPayMethod();
    PayDetailEntity toEntity();
}
