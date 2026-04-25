package jongwon.e_commerce.payment.gateway.toss;

import jongwon.e_commerce.payment.gateway.PaymentApprovalExecutor;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.gateway.PaymentClient;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@Service
@Builder
@Slf4j
public class TossPaymentExecutor implements PaymentApprovalExecutor {

    private final PaymentClient tossPaymentClient;
    private final TossExceptionTranslator tossExceptionTranslator;

    public TossPaymentExecutor(@Qualifier("tossPaymentClient")PaymentClient paymentClient, TossExceptionTranslator tossExceptionTranslator){
        this.tossPaymentClient = paymentClient;
        this.tossExceptionTranslator = tossExceptionTranslator;
    }

    @Override
    public PayApproveOutcome executePayApprove(PayApproveAttempt request){
        try{
            log.info("event = PG_API_CALL_START " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            PayResult payResult = tossPaymentClient.callPayApprovalApi(request);

            log.info("event = PG_API_CALL_SUCCESS " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            return new PayApproveSuccess(payResult);
        } catch(RestClientException e){
            return tossExceptionTranslator.translate(e);
        }
    }

    @Override
    public boolean supports(String providerName) {
        return "TOSS".equals(providerName);
    }
}
