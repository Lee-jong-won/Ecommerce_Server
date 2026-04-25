package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentApprovalExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PGCaller {

    private final List<PaymentApprovalExecutor> paymentApprovalExecutors;

    public PayApproveOutcome processPayApprove(String pgType, PayApproveAttempt payApproveAttempt){
        PaymentApprovalExecutor paymentApprovalExecutor = paymentApprovalExecutors.stream().
                filter(p -> p.supports(pgType))
                .findFirst().orElseThrow();
        return paymentApprovalExecutor.executePayApprove(payApproveAttempt);
    }

}
