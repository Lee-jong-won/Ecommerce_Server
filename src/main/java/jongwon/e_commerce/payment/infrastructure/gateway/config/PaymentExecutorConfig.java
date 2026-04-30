package jongwon.e_commerce.payment.infrastructure.gateway.config;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.infrastructure.gateway.CommonPaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentClient;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler.TossExceptionTranslator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentExecutorConfig {

    @Bean(name = "tossPaymentExecutor")
    public PaymentExecutor tossPaymentExecutor(@Qualifier("tossPaymentClient") PaymentClient tossClient,
                                               TossExceptionTranslator tossTranslator){
        return new CommonPaymentExecutor(tossClient, tossTranslator, PGType.TOSS);
    }
}
