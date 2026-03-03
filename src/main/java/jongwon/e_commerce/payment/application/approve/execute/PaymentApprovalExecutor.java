package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentApprovalExecutor {
    private final PaymentApprovalPGCaller paymentApprovalPGCaller;
    public PayApproveOutcome execute(
            PayApproveAttempt attempt,
            String idempotencyKey
    ){
        try{
            TossPaymentApproveResponse response = paymentApprovalPGCaller.callPayApproveApi(attempt, idempotencyKey);
            return new PayApproveSuccess(response.toPayResult());
        } catch(TossPaymentTimeoutException e){
            return new PayApproveTimeout();
        } catch(TossPaymentException e){
            return new PayApproveFail();
        }
    }
}
