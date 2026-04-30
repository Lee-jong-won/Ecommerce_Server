package jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler;

import jongwon.e_commerce.payment.infrastructure.gateway.exhandler.AbstractPayExceptionTranslator;
import jongwon.e_commerce.payment.infrastructure.gateway.exhandler.PaymentErrorResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TossExceptionTranslator extends AbstractPayExceptionTranslator {
    private final PaymentErrorResponseHandler tossErrorResponseHandler;

    public TossExceptionTranslator(
            @Qualifier("tossErrorResponseHandler") TossErrorResponseHandler tossErrorResponseHandler) {
        this.tossErrorResponseHandler = tossErrorResponseHandler;
    }

    @Override
    protected PaymentErrorResponseHandler getErrorResponseHandler() {
        return this.tossErrorResponseHandler;
    }

}
