package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.payment.application.approve.process2.PaymentApprovalApiCaller;
import jongwon.e_commerce.payment.application.approve.process3.fail.PaymentFailProcessor;
import jongwon.e_commerce.payment.application.approve.process3.success.PaymentSuccessProcessor;
import jongwon.e_commerce.payment.application.approve.process3.timeout.PaymentTimeoutProcessor;
import jongwon.e_commerce.payment.application.approve.process1.PaymentApprovalPreprocessor;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalService {

    // 결제 전처리부
    private final PaymentApprovalPreprocessor paymentApprovalPreprocessor;

    // 외부 api 호출부
    private final PaymentApprovalApiCaller paymentApprovalApiCaller;

    // 외부 api 호출 결과에 따른, 후처리부
    private final PaymentSuccessProcessor paymentSuccessProcessor;
    private final PaymentFailProcessor paymentFailProcessor;
    private final PaymentTimeoutProcessor paymentTimeoutProcessor;

    public void approvePayment(TossPaymentApproveRequest request){
        //1. 결제 전처리
        paymentApprovalPreprocessor.preparePaymentApproval(request.getPaymentKey(), request.getOrderId(), request.getAmount());
        try {
            // 2. 결제 승인 api 호출
            TossPaymentApproveResponse response = paymentApprovalApiCaller.callPayApproveApi(request, UUID.randomUUID().toString());
            // 3-1. 성공 시, 성공 상태 DB에 반영
            paymentSuccessProcessor.completeSuccess(response);
        } catch(TossPaymentTimeoutException e){
            // 3-2. 타임 아웃 발생 시, 타임 아웃 상태 DB에 반영
            paymentTimeoutProcessor.completeTimeout(request.getPaymentKey(), request.getOrderId());
        } catch(TossPaymentException e){
            // 3-3. 그 외 예외 발생 시, 실패 상태로 DB에 반영
            paymentFailProcessor.completeFail(request.getPaymentKey(), request.getOrderId());
        }
    }
}
