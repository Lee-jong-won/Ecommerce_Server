package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayApprovalExecutorException {

    private final TossPaymentClient tossPaymentClient;
    private final @Qualifier("tossRetryTemplate") RetryOperations tossPayApproveRetryOperation;
    private final DefaultPayApproveExceptionTranslatorException defaultPayApproveExceptionTranslatorException;

    public PayApproveSuccess executePayApprove(PayApproveAttempt request){
        try{
            TossPaymentApproveResponse response = tossPayApproveRetryOperation.execute(context ->
                    tossPaymentClient.callPayApprovalApi(request, UUID.randomUUID().toString())
            );
            return new PayApproveSuccess(response.toPayResult());
        } catch(RestClientException e){
            throw defaultPayApproveExceptionTranslatorException.translate(e);
        }
    }
}
