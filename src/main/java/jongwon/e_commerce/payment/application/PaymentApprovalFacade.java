package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalClientFailException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalPGFailException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentApprovalFacade {
    private final PaymentPrepareService paymentPrepareService;
    private final PaymentResultService paymentResultService;
    private final TossPaymentClient tossPaymentClient;

    public void approvePayment(TossPaymentApproveRequest request){
        //1.준비 단계
        paymentPrepareService.preparePayment(request);
        try{
            //2.외부 api 호출
            TossPaymentApproveResponse response = tossPaymentClient.approvePayment(
                    request.getPaymentKey(),
                    request.getPayOrderId(),
                    request.getAmount()
            );
            //3.결과 반영
            safeExecute(
                    "applySuccess",
                    request.getPaymentKey(),
                    null,
                    () -> paymentResultService.applySuccess(request.getPaymentKey(), response)
            );
        } catch( TossPaymentApprovalClientFailException e){
            // 클라이언트 오류 → 결제 실패 처리
            safeExecute(
                    "applyFail",
                    request.getPaymentKey(),
                    e,
                    () -> paymentResultService.applyFail(request.getPaymentKey())
            );
            throw e;

        } catch( TossPaymentApprovalPGFailException e){
            // PG 서버 오류 → 재시도 가능 영역
            safeExecute(
                    "applyFail",
                    request.getPaymentKey(),
                    e,
                    () -> paymentResultService.applyFail(request.getPaymentKey())
            );
            throw e;

        } catch( TossApiNetworkException e ){
            // 타임 아웃 -> 재시도 가능 영역
            safeExecute(
                    "applyTimeout",
                    request.getPaymentKey(),
                    e,
                    () -> paymentResultService.applyTimeout(request.getPaymentKey())
            );
            throw e;
        }
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
