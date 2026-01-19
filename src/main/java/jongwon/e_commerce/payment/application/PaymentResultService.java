package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.infra.PaymentRepository;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentResultService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void applySuccess(String paymentKey,
                             TossPaymentApproveResponse response) {
        Pay payment = getPayByPaymentKey(paymentKey);

        payment.markSuccess();
        payment.setPayMethod(PayMethod.valueOf(response.getMethod()));
        payment.setApprovedAt(response.getApprovedAt());
        payment.setRequestedAt(response.getRequestedAt());
    }


    @Transactional
    public void applyFail(String paymentKey) {
        Pay payment = getPayByPaymentKey(paymentKey);
        payment.markFailed();
    }

    @Transactional
    public void applyTimeout(String paymentKey){
        Pay payment = getPayByPaymentKey(paymentKey);
        payment.markSyncTimeout();
    }

    private Pay getPayByPaymentKey(String paymentKey) {
        Pay payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 존재하지 않습니다!"));
        return payment;
    }
}
