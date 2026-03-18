package jongwon.e_commerce.payment.application.approve.settlement.handler.success;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.MobilePhonePayRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentResultApplier {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Pay applyPayResult(Pay pay, PayResult payResult){
        // 1. 결제 상태 반영
        pay.recordPayResult(payResult);
        pay.comeplete();
        paymentRepository.save(pay);

        // 2. 결제 수단별 상세 정보 저장
        PayMethod payMethod = pay.getPayMethod();
        PaymentDetail paymentDetail = payResult.getPaymentDetail();
        switch(payMethod){
            case MOBILE -> {

                MPPay mpPay = (MPPay)paymentDetail;

                // paymentDetail을 Pay를 이용해, jpaEntity로 변환한 후 저장 -> 반환은 도메인 객체로
                // jpaEntity로 변환 시, pay도 jpa Entity로 변환 해야함
                // MPPay Jpa Entity -> Pay 객체로 변환 후 return (테스트를 위해)
            }
        }
    }
}
