package jongwon.e_commerce.payment.application.approve.settlement.handler.success;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentResultApplier {

    private final PaymentRepository paymentRepository;
    private final PayDetailSaver payDetailSaver;

    @Transactional
    public PaymentDetail applyPayResult(Pay pay, PayResult payResult){

        // 1. PG 에서의 결제 결과 반영
        pay.recordPayResult(payResult);

        // 2. 결제 상태 변경
        pay.comeplete();

        // 3. 결제 공통 정보 업데이트
        Pay updatedPay = paymentRepository.save(pay);

        // 4. 결제 상세 정보 저장
        PaymentDetail paymentDetail = payResult.getPaymentDetail();
        paymentDetail.setPay(updatedPay);
        PaymentDetail savedPaymentDetail = payDetailSaver.save(paymentDetail);

        return savedPaymentDetail;
    }
}
