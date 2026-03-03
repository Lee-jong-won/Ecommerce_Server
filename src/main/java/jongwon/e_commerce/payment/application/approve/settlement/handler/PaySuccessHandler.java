package jongwon.e_commerce.payment.application.approve.settlement.handler;

import jongwon.e_commerce.payment.application.approve.settlement.handler.success.OrderStockProcessor;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaySuccessHandler implements PayOutcomeHandler {

    private final PaymentRepository paymentRepository;
    private final OrderStockProcessor orderStockProcessor;

    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.SUCCESS;
    }

    @Override
    @Transactional
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        // 1. 결제 결과를 상태에 반영
        pay.recordPayResult(((PayApproveSuccess) outcome).getPayResult());

        // 2. 재고 감소
        orderStockProcessor.deductStockBy(pay);

        // 3. 결제 결과 DB에 저장
        paymentRepository.save(pay);

        // 4. 응답 생성
        return new PayApproveOutcomeResponse(
                PayStatus.COMPLETE,
                "PAYMENT_SUCCESS",
                "결제에 성공했습니다"
        );
    }
}
