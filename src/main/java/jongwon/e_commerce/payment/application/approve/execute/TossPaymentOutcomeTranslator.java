package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.*;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TossPaymentOutcomeTranslator {
    public PayApproveOutcome translate(Supplier<TossPaymentApproveResponse> call) {
        try {
            TossPaymentApproveResponse response = call.get();
            PayResult payResult = response.toPayResult();
            return new PayApproveSuccess(payResult);
        } catch (TossPaymentTimeoutException e) {
            return new PayApproveTimeout();
        } catch (TossPaymentException e) {
            return new PayApproveFail();
        }
    }
}
