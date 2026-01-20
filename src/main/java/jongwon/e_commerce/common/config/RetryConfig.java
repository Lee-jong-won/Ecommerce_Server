package jongwon.e_commerce.common.config;

import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean(name = "tossRetryTemplate")
    public RetryTemplate paymentApproveRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        // 재시도 정책: 최대 3회
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(
                3,
                Map.of(
                        TossPaymentRetryableException.class, true
                )
        );

        template.setRetryPolicy(retryPolicy);

        // 고정 간격 1초 대기
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000);

        template.setBackOffPolicy(backOffPolicy);

        return template;
    }

}
