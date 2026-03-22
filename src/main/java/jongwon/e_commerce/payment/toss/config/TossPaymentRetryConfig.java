package jongwon.e_commerce.payment.toss.config;

import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Configuration
@EnableRetry
public class TossPaymentRetryConfig {
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(MAX_RETRY_ATTEMPTS);
    private final NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

    @Bean(name = "tossRetryTemplate")
    public RetryTemplate paymentApproveRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(configureRetryPolicy());
        retryTemplate.setRetryPolicy(policy);

        // 고정 간격 1초 대기
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    private Classifier<Throwable, RetryPolicy> configureRetryPolicy() {
        return throwable -> {
            // 1️⃣ HTTP 상태 코드 기반 retry
            if (throwable instanceof RestClientResponseException ex) {
                return getRetryPolicyForStatus(ex.getStatusCode().value());
            }

            // 2️⃣ ResourceAccessException (네트워크 계열)
            if (throwable instanceof ResourceAccessException ex) {
                return isReadTimeout(ex) ? simpleRetryPolicy : neverRetryPolicy;
            }

            // 3️⃣ 나머지는 retry 안함
            return neverRetryPolicy;
        };
    }

    private RetryPolicy getRetryPolicyForStatus(int value) {
        if (value == 502 || value == 503 || value == 500 || value == 504 || value == 429) {
            return simpleRetryPolicy;
        }
        return neverRetryPolicy;
    }

    private boolean isReadTimeout(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
