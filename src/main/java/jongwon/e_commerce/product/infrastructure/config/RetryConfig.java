package jongwon.e_commerce.product.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {

    @Bean(name = "stockRetryTemplate")
    public RetryOperations retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 1. 재시도 정책: 낙관적 락 충돌 시 최대 3번까지 재시도
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(ObjectOptimisticLockingFailureException.class, true);

        SimpleRetryPolicy retryPolicy =
                new SimpleRetryPolicy(3, retryableExceptions);

        // 2. 백오프 정책: 충돌 시 100ms 대기 후 재시도
        UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
        backOffPolicy.setMinBackOffPeriod(200L);
        backOffPolicy.setMaxBackOffPeriod(400L);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

}
