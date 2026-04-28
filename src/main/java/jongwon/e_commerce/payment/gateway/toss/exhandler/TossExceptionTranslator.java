package jongwon.e_commerce.payment.gateway.toss.exhandler;

import jongwon.e_commerce.payment.gateway.exhandler.AbstractPayExceptionTranslator;
import jongwon.e_commerce.payment.gateway.exhandler.NetworkExceptionHandler;
import jongwon.e_commerce.payment.gateway.exhandler.PaymentExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TossExceptionTranslator extends AbstractPayExceptionTranslator {

    private final PaymentExceptionHandler tossErrorResponseHandler;

    public TossExceptionTranslator(
            @Qualifier("networkExceptionHandler") NetworkExceptionHandler networkHandler,
            @Qualifier("tossErrorResponseHandler") TossErrorResponseHandler tossErrorResponseHandler) {
        super(networkHandler); // 모든 PG사와 통신 시 발생할 수 있는 네트워크 예외 처리를 담당하는 핸들러
        this.tossErrorResponseHandler = tossErrorResponseHandler;
    }

    @Override
    protected PaymentExceptionHandler getErrorResponseHandler() {
        return this.tossErrorResponseHandler;
    }

}
