package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import jongwon.e_commerce.payment.toss.TossApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossApiService {

    private final TossApiCaller tossApiCaller;
    private final @Qualifier("tossRetryTemplate")RetryTemplate retryTemplate;

    public TossPaymentApproveResponse approve(TossPaymentApproveRequest request, String idempotencyKey){
        return retryTemplate.execute(context -> {
            TossPaymentApproveResponse response  = tossApiCaller.approve(request, idempotencyKey);
            return response;
        });
    }

    public void cancel(TossPaymentCancelRequest request) {
        retryTemplate.execute(context -> {
            tossApiCaller.cancel(
                    request.getPaymentKey(),
                    request.getCancelReason(),
                    request.getIdempotencyKey()
            );
            return null;
        });
    }
}
