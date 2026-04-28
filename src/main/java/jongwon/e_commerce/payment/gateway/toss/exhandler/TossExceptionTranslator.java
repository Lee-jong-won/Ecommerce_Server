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
    private final List<PaymentExceptionHandler> handlers;
    public TossExceptionTranslator(
            @Qualifier("networkExceptionHandler") NetworkExceptionHandler networkHandler,
            @Qualifier("tossErrorResponseHandler") TossErrorResponseHandler tossHandler) {
        this.handlers = List.of(networkHandler, tossHandler);
    }

    @Override
    protected List<PaymentExceptionHandler> getHandlers() {
        return this.handlers;
    }

}
