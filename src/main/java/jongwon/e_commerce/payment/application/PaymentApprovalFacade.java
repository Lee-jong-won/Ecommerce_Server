package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalClientFailException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalPGFailException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
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
            paymentResultService.applySuccess(request.getPaymentKey(), response);
        } catch( TossPaymentApprovalClientFailException e){
            // 클라이언트 오류 → 결제 실패 처리
            paymentResultService.applyFail(request.getPaymentKey());
            throw e;

        } catch( TossPaymentApprovalPGFailException e){
            // PG 서버 오류 → 재시도 가능 영역
            paymentResultService.applyFail(request.getPaymentKey());
            throw e;

        } catch( TossApiNetworkException e ){
            // 타임 아웃 -> 재시도 가능 영역
            paymentResultService.applyTimeout(request.getPaymentKey());
            throw e;
        }
    }
}
