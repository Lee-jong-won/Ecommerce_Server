package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PayApprovalExecutor {

    private final TossPaymentClient tossPaymentClient;
    private final @Qualifier("tossRetryTemplate") RetryOperations tossPayApproveRetryOperation;
    private final PayApproveExceptionTranslator payApproveExceptionTranslator;

    public PayApproveOutcome executePayApprove(PayApproveAttempt request){
        try{
            TossPaymentApproveResponse response = tossPayApproveRetryOperation.execute(context ->
                    tossPaymentClient.callPayApprovalApi(request, UUID.randomUUID().toString())
            );
            return new PayApproveSuccess(response.toPayResult());
        } catch(RestClientException e){
            return payApproveExceptionTranslator.translate(e);
        }
    }
}
