package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.infrastructure.gateway.exhandler.AbstractPayExceptionTranslator;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClientException;


@Builder
@Slf4j
public class CommonPaymentExecutor implements PaymentExecutor {

    private final PaymentClient paymentClient;
    private final AbstractPayExceptionTranslator exceptionTranslator;
    private final PGType pgType;
    public CommonPaymentExecutor(PaymentClient paymentClient,
                                 AbstractPayExceptionTranslator exceptionTranslator,
                                 PGType pgType){
        this.paymentClient = paymentClient;
        this.exceptionTranslator = exceptionTranslator;
        this.pgType = pgType;
    }

    @Override
    public PayApproveOutcome executePayApprove(PayApproveAttempt request){

        try{
            log.info("event = PG_API_CALL_START " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            PayApproveOutcome payApproveOutcome = paymentClient.callPayApprovalApi(request);

            log.info("event = PG_API_CALL_SUCCESS " + "paymentKey = {} " + "amount = {} ",
                    request.getPaymentKey(), request.getAmount());

            return payApproveOutcome;
        } catch(RestClientException e){
            return exceptionTranslator.translate(e);
        }

    }

    @Override
    public boolean supports(PGType pgType) {
        return this.pgType == pgType;
    }
}
