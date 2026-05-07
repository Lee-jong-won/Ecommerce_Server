package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class PaymentCreator {

    private final PaymentRepository paymentRepository;
    private final PayRequestRepository payRequestRepository;
    @Transactional
    public Pay reflectPaySuccessResult(PayRequest payRequest, PayResult payResult){
        payRequest.complete();
        payRequestRepository.save(payRequest);
        PayResult.PayResultCommon payResultCommon = payResult.getPayResultCommon();
        Map<String, Object> paymentDetail = payResult.getPaymentDetail();
        Pay pay = Pay.from(payResultCommon, paymentDetail, payRequest);
        return paymentRepository.save(pay);
    }
}
