package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.result.success.PayApproveSuccess;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class PayApprovalExecutor {

    private final PaymentApproveClient paymentApproveClient;
    private final PayApproveExceptionTranslator payApproveExceptionTranslator;

    public PayApproveOutcome executePayApprove(PayApproveAttempt request, String idempotencyKey){
        try{

            log.info("event = PG_API_CALL_START " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            TossPaymentApproveResponse response = paymentApproveClient.callPayApprovalApi(request,
                    idempotencyKey);

            log.info("event = PG_API_CALL_SUCCESS " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            return new PayApproveSuccess(response.toPayResult());
        } catch(RestClientException e){
            return payApproveExceptionTranslator.translate(e);
        }
    }
}
