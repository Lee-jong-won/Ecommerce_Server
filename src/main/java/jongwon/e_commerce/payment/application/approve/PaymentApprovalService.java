package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.application.PayProcessStateManager;
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
    private final PayProcessStateManager payProcessStateManager;
    private final List<PaymentExecutor> paymentExecutors;
    private final PaySuccessProcessor paySuccessProcessor;

    public PayResult approvePayment(PayApproveAttempt attempt) {
        log.info("event = PAYMENT_START paymentKey = {} amount = {}",
                attempt.getPaymentKey(), attempt.getAmount());

        PayRequest payRequest = payPreprocessor.preProcess(attempt);

        PGType pgType = PGType.from(attempt.getPgType());
        PaymentExecutor executor = paymentExecutors.stream()
                .filter(e -> e.supports(pgType))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("등록되지 않은 PG사로 결제 승인 요청 불가: {}", pgType);
                    return new UnsupportedOperationException("지원하지 않는 결제 수단입니다.");
                });

        try {
            PayResult result = executor.executePayApprove(attempt);
            paySuccessProcessor.process(payRequest, result);
            payProcessStateManager.processSuccess(payRequest);
            log.info("event = PAYMENT_FINISHED paymentKey = {} amount = {}",
                    attempt.getPaymentKey(), attempt.getAmount());
            return result;
        } catch (PayUnknownOutcomeException e) {
            payProcessStateManager.processUnknownOutcome(payRequest);
            throw e;
        } catch (PayClientException e) {
            payProcessStateManager.processBusinessFailed(payRequest);
            throw e;
        } catch (PayServerException e){
            payProcessStateManager.processServerFailed(payRequest);
            throw e;
        } catch(PayGatewayException e) {
            payProcessStateManager.processPGFailed(payRequest);
            throw e;
        }
    }
}
