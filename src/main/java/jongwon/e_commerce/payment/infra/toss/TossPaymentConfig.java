package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.infra.toss.TossPaymentProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentConfig {
}
