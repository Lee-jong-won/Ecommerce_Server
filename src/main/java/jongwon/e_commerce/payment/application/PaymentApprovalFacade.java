package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentCancelRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                tossPaymentClient.approve(request)
        );
    }

    private void callCancelApi(TossPaymentCancelRequest request){
        tossRetryTemplate.execute(context -> {
            tossPaymentClient.cancel(request);
            return null;
        });
    }

    private void handleSuccess(TossPaymentApproveRequest request,
                               TossPaymentApproveResponse response) {

        boolean dbSuccessApplied = safeExecute(
                "applySuccess",
                request.getPaymentKey(),
                () -> paymentResultService.applySuccess(
                        request.getPaymentKey(),
                        request.getOrderId(),
                        response
                )
        );

        if(!dbSuccessApplied){
            callCancelApi(new TossPaymentCancelRequest(request.getPaymentKey(),
                    "DB 반영 실패로 인한 결제 취소", UUID.randomUUID().toString()));
        }
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
                () -> paymentResultService.applyFail(
                        request.getPaymentKey(),
                        request.getOrderId()
                )
        );
    }

    private boolean safeExecute(
            String actionName,
            String paymentKey,
            Runnable action
    ){
        try {
            action.run();
            return true;
        } catch (DataAccessException dataAccessException) {
            log.error(
                    "[CRITICAL] 결제 상태 반영 실패 - action: {}, paymentKey: {}, originalException: {}, dbException: {}",
                    actionName,
                    paymentKey,
                    dataAccessException.getMessage()
            );
            return false;
        }
    }

}
