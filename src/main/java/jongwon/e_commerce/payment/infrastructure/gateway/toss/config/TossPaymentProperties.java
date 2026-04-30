package jongwon.e_commerce.payment.infrastructure.gateway.toss.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
@Getter
@Setter
public class TossPaymentProperties {
    private String secretKey;
    private String apiUrl;
    private String basicAuthPrefix;
}
