package jongwon.e_commerce.payment.application.approve.result.persistence;

import jongwon.e_commerce.payment.domain.MPPay;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import jongwon.e_commerce.payment.repository.MobilePhonePayRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentRecorderImpl implements PaymentRecorder {

    private final PaymentRepository paymentRepository;
    private final MobilePhonePayRepository mobilePhonePayRepository;
    @Override
    public void record(Pay pay) {
        paymentRepository.save(pay);
        PayMethod method = pay.getPayMethod();
        switch(method) {
            case MOBILE -> mobilePhonePayRepository.save((MPPay) pay);
            default -> throw new UnsupportedPayMethodException("지원하지 않은 결제 타입입니다");
        }
    }
}
