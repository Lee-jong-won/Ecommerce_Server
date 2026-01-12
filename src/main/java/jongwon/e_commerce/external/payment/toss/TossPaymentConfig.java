package jongwon.e_commerce.external.payment.toss;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentConfig {
}
