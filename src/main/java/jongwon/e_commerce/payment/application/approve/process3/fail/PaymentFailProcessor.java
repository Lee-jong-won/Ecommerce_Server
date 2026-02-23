package jongwon.e_commerce.payment.application.approve.process3.fail;

import jongwon.e_commerce.payment.application.approve.process3.common.PaymentStateUpdater;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentFailProcessor {
    private final PaymentStateUpdater paymentStateUpdater;
    @Transactional
    public void completeFail(String paymentKey, String orderId){
        try{
            paymentStateUpdater.applyFail(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }
}
