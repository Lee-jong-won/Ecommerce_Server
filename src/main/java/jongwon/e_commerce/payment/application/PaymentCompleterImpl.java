package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompleterImpl implements PaymentCompleter {

    private final StockChanger stockChanger;
    private final PaymentResultUpdater paymentResultUpdater;

    @Override
    public void completeSuccess(String paymentKey,
                                String orderId,
                                OffsetDateTime approvedAt,
                                String method){
        try{
            paymentResultUpdater.applySuccess(orderId, approvedAt, method);
            stockChanger.decreaseStock(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }

    @Override
    public void completeTimeout(String paymentKey, String orderId){
        try{
            paymentResultUpdater.applyTimeout(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }

    @Override
    public void completeFail(String paymentKey, String orderId){
        try{
            paymentResultUpdater.applyFail(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }
}
