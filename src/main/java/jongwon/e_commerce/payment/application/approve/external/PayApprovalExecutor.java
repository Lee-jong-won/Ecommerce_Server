package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PayApprovalExecutor {

    private final TossPaymentClient tossPaymentClient;
    private final PayApproveExceptionTranslator payApproveExceptionTranslator;

    public PayApproveOutcome executePayApprove(PayApproveAttempt request){
        try{
            TossPaymentApproveResponse response = tossPaymentClient.callPayApprovalApi(request,
                    UUID.randomUUID().toString());
            return new PayApproveSuccess(response.toPayResult());
        } catch(RestClientException e){
            return payApproveExceptionTranslator.translate(e);
        }
    }
}
