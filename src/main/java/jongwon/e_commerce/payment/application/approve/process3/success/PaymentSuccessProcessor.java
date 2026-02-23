package jongwon.e_commerce.payment.application.approve.process3.success;

import jongwon.e_commerce.payment.application.approve.process3.common.PaymentStateUpdater;
import jongwon.e_commerce.payment.application.approve.process3.success.detailcreator.PaymentDetailCreatorService;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessProcessor {

    private final StockChanger stockChanger;
    private final PaymentDetailCreatorService paymentDetailCreatorService;
    private final PaymentStateUpdater paymentStateUpdater;

    @Transactional
    public void completeSuccess(TossPaymentApproveResponse response){
        try{
            paymentStateUpdater.applySuccess(response.getOrderId(), response.getApprovedAt(), response.getMethod());
            paymentDetailCreatorService.saveDetail(response);
            stockChanger.decreaseStock(response.getOrderId());
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(response.getPaymentKey(), response.getOrderId(), e);
        }
    }
}
