package jongwon.e_commerce.payment.application.approve.settlement.handler;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.payment.application.approve.settlement.handler.success.PaymentResultApplier;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaySuccessHandler implements PayOutcomeHandler {

    private final PaymentResultApplier paymentResultApplier;
    private final OrderStockProcessor orderStockProcessor;

    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.SUCCESS;
    }

    @Override
    @Transactional
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        // 2. 결제 결과 반영
        paymentResultApplier.applyPayResult(pay, ((PayApproveSuccess) outcome).getPayResult());

        // 3. 재고 감소
        orderStockProcessor.deductStockOf(pay.getOrder());

        // 4. 응답 생성
        return new PayApproveOutcomeResponse(
                PayStatus.COMPLETE,
                "PAYMENT_SUCCESS",
                "결제에 성공했습니다"
        );
    }
}
