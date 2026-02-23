package jongwon.e_commerce.payment.application.approve.process3.success.detailcreator;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.repository.MobilePhonePayDetailRepository;

public class MobilePhonePayDetailCreator implements PaymentDetailCreator{
    MobilePhonePayDetailRepository mobilePhonePayDetailRepository;
    @Override
    public PayMethod supports() {
        return PayMethod.MOBILE;
    }

    @Override
    public void save(Long fkPayId, TossPaymentApproveResponse response) {
        TossPaymentApproveResponse.MobilePhoneDto dto = response.getMobilePhone();
        mobilePhonePayDetailRepository.save(fkPayId, dto);
    }
}
