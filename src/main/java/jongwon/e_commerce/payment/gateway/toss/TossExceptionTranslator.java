package jongwon.e_commerce.payment.gateway.toss;

import jongwon.e_commerce.payment.gateway.exhandler.AbstractPayExceptionTranslator;
import jongwon.e_commerce.payment.gateway.exhandler.NetworkExceptionHandler;
import jongwon.e_commerce.payment.gateway.exhandler.PGErrorCodeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TossExceptionTranslator extends AbstractPayExceptionTranslator {

    public TossExceptionTranslator(NetworkExceptionHandler networkExceptionHandler) {
        super(networkExceptionHandler);
    }

    @Override
    protected PGErrorCodeParser getErrorCodeParser() {
        return super.getErrorCodeParser();
    }

}
