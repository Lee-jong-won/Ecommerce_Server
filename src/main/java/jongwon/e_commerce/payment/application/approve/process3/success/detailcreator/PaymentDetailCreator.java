package jongwon.e_commerce.payment.application.approve.process3.success.detailcreator;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;

public interface PaymentDetailCreator {
    PayMethod supports();
    void save(Long fkPayId, TossPaymentApproveResponse response);
}
