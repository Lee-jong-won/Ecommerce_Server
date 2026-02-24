package jongwon.e_commerce.payment.application.approve.result.persistence;

import jongwon.e_commerce.payment.application.approve.result.context.MobilePhoneDetail;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentDetail;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentDetailCreator;
import jongwon.e_commerce.payment.domain.MobilePhonePay;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.repository.MobilePhonePayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MobilePhoneCreator implements PaymentDetailCreator {

    private final MobilePhonePayRepository mobilePhonePayRepository;

    @Override
    public PayMethod supports() {
        return PayMethod.MOBILE;
    }

    @Override
    public void createDetailOf(Pay pay, PaymentDetail detail) {
        MobilePhonePay mobilePhonePay = MobilePhonePay.create(pay, (MobilePhoneDetail) detail);
        mobilePhonePayRepository.save(mobilePhonePay);
    }
}
