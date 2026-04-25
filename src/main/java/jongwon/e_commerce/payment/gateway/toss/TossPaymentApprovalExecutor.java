package jongwon.e_commerce.payment.gateway.toss;

import jongwon.e_commerce.payment.gateway.PaymentApprovalExecutor;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.gateway.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.gateway.PayApproveExceptionTranslator;
import jongwon.e_commerce.payment.gateway.PaymentApproveClient;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class TossPaymentApprovalExecutor implements PaymentApprovalExecutor {

    private final PaymentApproveClient paymentApproveClient;
    private final PayApproveExceptionTranslator payApproveExceptionTranslator;

    @Override
    public PayApproveOutcome executePayApprove(PayApproveAttempt request){
        try{
            log.info("event = PG_API_CALL_START " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            PayResult payResult = paymentApproveClient.callPayApprovalApi(request);

            log.info("event = PG_API_CALL_SUCCESS " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            return new PayApproveSuccess(payResult);
        } catch(RestClientException e){
            return payApproveExceptionTranslator.translate(e);
        }
    }

    @Override
    public boolean supports(String providerName) {
        return "TOSS".equals(providerName);
    }
}
