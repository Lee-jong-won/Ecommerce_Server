package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalFacade {
    private final PreparePaymentApprovalService preparePaymentApprovalService;
    private final TossPaymentGateWay tossPaymentGateWay;
    private final PaymentCompleteService paymentCompleteService;
    public void approvePayment(TossPaymentApproveRequest request){
        //1. 결제 전처리
        preparePaymentApprovalService.preparePaymentApproval(request.getOrderId(), request.getAmount());
        try {
            // 2. 결제 승인 api 호출
            TossPaymentApproveResponse response = tossPaymentGateWay.payApprove(request, UUID.randomUUID().toString());
            // 3-1. 성공 시, 성공 상태 DB에 반영
            paymentCompleteService.completeSuccess(request.getPaymentKey(), request.getOrderId(),
                    response.getApprovedAt(), response.getMethod());
        } catch(TossPaymentTimeoutException e){
            // 3-2. 타임아웃 발생 시, 타임아웃 상태 DB에 반영
            paymentCompleteService.completeTimeout(request.getOrderId());
        } catch(TossPaymentException e){
            // 3-3. 그 외 예외 발생 시, 실패 상태로 DB에 반영
            paymentCompleteService.completeFail(request.getOrderId());
        }
    }

}
