package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PGCaller {

    private final List<PaymentExecutor> paymentExecutors;

    public PayApproveOutcome processPayApprove(String pgType, PayApproveAttempt payApproveAttempt){
        PaymentExecutor paymentExecutor = paymentExecutors.stream().
                filter(p -> p.supports(pgType))
                .findFirst().orElseThrow();
        return paymentExecutor.executePayApprove(payApproveAttempt);
    }

}
