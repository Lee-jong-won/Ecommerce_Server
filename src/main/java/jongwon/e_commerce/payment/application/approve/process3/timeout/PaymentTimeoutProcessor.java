package jongwon.e_commerce.payment.application.approve.process3.timeout;

import jongwon.e_commerce.payment.application.approve.process3.common.PaymentStateUpdater;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentTimeoutProcessor {

    private final PaymentStateUpdater paymentStateUpdater;
    @Transactional
    public void completeTimeout(String paymentKey, String orderId){
        try{
            paymentStateUpdater.applyTimeout(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }

}
