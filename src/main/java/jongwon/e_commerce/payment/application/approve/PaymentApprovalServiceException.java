package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.payment.application.approve.execute.PayApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.execute.PayApprovalExecutorException;
import jongwon.e_commerce.payment.application.approve.preprocess.PayPreprocessor;
import jongwon.e_commerce.payment.application.approve.settlement.handler.PayFailHandler;
import jongwon.e_commerce.payment.application.approve.settlement.handler.PaySuccessHandler;
import jongwon.e_commerce.payment.application.approve.settlement.handler.PayTimeoutHandler;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.exception.external.TossPaymentClientException;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentApprovalServiceException {

    private final PayPreprocessor payPreprocessor;
    private final PayApprovalExecutorException payApprovalExecutorException;
    private final PaySuccessHandler paySuccessHandler;
    private final PayFailHandler payFailHandler;
    private final PayTimeoutHandler payTimeoutHandler;

    public PayApproveOutcomeResponse approvePayment(PayApproveAttempt attempt){
        Pay pay = payPreprocessor.preProcess(attempt);
        try{
            PayApproveSuccess payApproveSuccess = payApprovalExecutorException.executePayApprove(attempt);
            return paySuccessHandler.handle(pay, payApproveSuccess);
        }catch(TossPaymentTimeoutException e){
            return payTimeoutHandler.handle(pay);
        }catch(TossPaymentException){
            return payFailHandler.handle(pay);
        }
    }
}
