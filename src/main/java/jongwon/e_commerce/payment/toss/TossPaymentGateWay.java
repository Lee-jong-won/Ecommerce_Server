package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
public class TossPaymentGateWay {

    private final TossPaymentHttpClient tossPaymentHttpClient;
    private final TossPaymentNetworkExceptionTranslator tossPaymentNetworkExceptionTranslator;
    private final @Qualifier("tossRetryTemplate")RetryTemplate retryTemplate;

    public TossPaymentApproveResponse approve(TossPaymentApproveRequest request, String idempotencyKey){
        return retryTemplate.execute(context -> {
            TossPaymentApproveResponse response;
            try{
                response = tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey);
            } catch( ResourceAccessException e){
                throw tossPaymentNetworkExceptionTranslator.translateNetworkException(e);
            }
            return response;
        });
    }

    public void cancel(TossPaymentCancelRequest request) {
        retryTemplate.execute(context -> {
            try {
                tossPaymentHttpClient.callPayCancelApi(
                        request.getPaymentKey(),
                        request.getIdempotencyKey(),
                        request.getCancelReason());
            } catch( ResourceAccessException e){
                throw tossPaymentNetworkExceptionTranslator.translateNetworkException(e);
            }
            return null;
        });
    }
}
