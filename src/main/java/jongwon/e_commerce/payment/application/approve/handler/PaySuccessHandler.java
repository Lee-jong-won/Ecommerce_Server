package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.payment.application.approve.PaymentService;
import jongwon.e_commerce.payment.application.approve.PayDetailSaver;
import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.dto.PaySuccessResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaySuccessHandler implements PayOutcomeHandler {

    private final OrderStockProcessor orderStockProcessor;
    private final PaymentService paymentService;
    private final PayDetailSaver payDetailSaver;

    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.SUCCESS;
    }

    @Override
    @Transactional
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        PayApproveSuccess payApproveSuccess = (PayApproveSuccess)outcome;
        PayResult payResult = payApproveSuccess.getPayResult();

        // 1. 결제 공통 정보 업데이트
        Pay updatedPay = paymentService.updatePayResult(pay.getId(), payResult.getPayResultCommon());

        // 2. 결제 상세 정보 저장
        payDetailSaver.save(updatedPay, payResult.getPaymentDetail());

        // 3. 재고 감소
        orderStockProcessor.deductStockOf(pay.getOrder());

        // 4. 응답 생성
        return new PaySuccessResponse(
                updatedPay.getPayStatus(),
                updatedPay.getPayMethod(),
                updatedPay.getPayAmount()
        );
    }
}
