package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentApprovalFacade {
    private final PaymentPrepareService paymentPrepareService;
    private final PaymentResultService paymentResultService;
    private final TossPaymentClient tossPaymentClient;
    private final RetryTemplate tossRetryTemplate;

    public PaymentApprovalFacade(PaymentPrepareService paymentPrepareService,
                                 PaymentResultService paymentResultService,
                                 TossPaymentClient tossPaymentClient,
                                 @Qualifier("tossRetryTemplate")RetryTemplate tossRetryTemplate){
        this.paymentPrepareService = paymentPrepareService;
        this.paymentResultService = paymentResultService;
        this.tossPaymentClient = tossPaymentClient;
        this.tossRetryTemplate = tossRetryTemplate;
    }

    public TossPaymentApproveResponse approvePayment(TossPaymentApproveRequest request){
        paymentPrepareService.preparePayment(request);
        try {
            TossPaymentApproveResponse response = callApproveApi(request);
            handleSuccess(request, response);
            return response;

        } catch (TossPaymentException e) {
            handleFailure(request, e);
            throw e;
        }
    }

    private TossPaymentApproveResponse callApproveApi(TossPaymentApproveRequest request) {
        return tossRetryTemplate.execute(context ->
                tossPaymentClient.approvePayment(
                        request.getPaymentKey(),
                        request.getPayOrderId(),
                        request.getAmount()
                )
        );
    }

    private void handleSuccess(TossPaymentApproveRequest request,
                               TossPaymentApproveResponse response) {
        safeExecute(
                "applySuccess",
                request.getPaymentKey(),
                null,
                () -> paymentResultService.applySuccess(
                        request.getPaymentKey(),
                        request.getOrderId(),
                        response
                )
        );
    }

    private void handleFailure(TossPaymentApproveRequest request, TossPaymentException e) {

        if (e instanceof TossPaymentUserFaultException) {
            handleUserFault(request, (TossPaymentUserFaultException) e);

        } else if (e instanceof TossPaymentRetryableException) {
            handleRetryableFault(request, (TossPaymentRetryableException) e);
        }
    }

    private void handleRetryableFault(TossPaymentApproveRequest request,
                                      TossPaymentRetryableException e) {
        if (e instanceof TossApiTimeoutException) {
            safeExecute(
                    "applyTimeout",
                    request.getPaymentKey(),
                    e,
                    () -> paymentResultService.applyTimeout(
                            request.getPaymentKey()
                    )
            );
        }
    }

    private void handleUserFault(TossPaymentApproveRequest request,
                                 TossPaymentUserFaultException e) {
        safeExecute(
                "applyFail",
                request.getPaymentKey(),
                e,
                () -> paymentResultService.applyFail(
                        request.getPaymentKey(),
                        request.getOrderId()
                )
        );
    }

    private void safeExecute(
            String actionName,
            String paymentKey,
            Exception originalException,
            Runnable action
    ){
        try {
            action.run();
        } catch (Exception dbException) {
            log.error(
                    "[CRITICAL] 결제 상태 반영 실패 - action: {}, paymentKey: {}, originalException: {}, dbException: {}",
                    actionName,
                    paymentKey,
                    originalException.getClass().getSimpleName(),
                    dbException.getMessage(),
                    dbException
            );
        }
    }

}
