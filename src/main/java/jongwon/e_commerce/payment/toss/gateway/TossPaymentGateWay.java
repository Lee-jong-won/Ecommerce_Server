package jongwon.e_commerce.payment.toss.gateway;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import jongwon.e_commerce.payment.dto.TossPaymentCancelResponse;
import jongwon.e_commerce.payment.toss.TossPaymentHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentGateWay {

    private final TossPaymentHttpClient tossPaymentHttpClient;
    private final @Qualifier("tossRetryTemplate")RetryTemplate retryTemplate;

    public TossPaymentApproveResponse payApprove(TossPaymentApproveRequest request, String idempotencyKey){
        return retryTemplate.execute(context -> {
            TossPaymentApproveResponse response = tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey);
            return response;
        });
    }

    public TossPaymentCancelResponse payCancel(TossPaymentCancelRequest request) {
        return retryTemplate.execute(context -> {
            TossPaymentCancelResponse response = tossPaymentHttpClient.callPayCancelApi(request.getPaymentKey(),
                    request.getCancelReason(), request.getIdempotencyKey());
            return response;
        });
    }
}
