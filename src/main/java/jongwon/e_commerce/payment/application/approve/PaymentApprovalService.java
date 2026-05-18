package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.payment.application.PayFailureHandler;
import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.exception.*;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalService {

    private final PayPreprocessor payPreprocessor;
    private final PayFailureHandler payFailureHandler;
    private final List<PaymentExecutor> paymentExecutors;
    private final PaySuccessHandler paySuccessHandler;

    public PayResult approvePayment(int expectedAmount, PayApproveAttempt attempt) {

        PayRequest payRequest = payPreprocessor.preProcess(expectedAmount, attempt);

        PaymentExecutor executor = paymentExecutors.stream()
                .filter(e -> e.supports(PGType.from(attempt.getPgType())))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 결제 수단입니다."));

        try {
            PayResult result = executor.executePayApprove(attempt);
            paySuccessHandler.process(payRequest.getId(), result);
            return result;
        } catch (PayUnknownOutcomeException e) {
            payFailureHandler.processUnknownOutcome(payRequest.getId());
            throw e;
        } catch (PayClientException e) {
            payFailureHandler.processBusinessFailed(payRequest.getId());
            throw e;
        } catch (PayServerException e){
            payFailureHandler.processServerFailed(payRequest.getId());
            throw e;
        } catch(PayGatewayException e) {
            payFailureHandler.processPGFailed(payRequest.getId());
            throw e;
        }
    }
}
