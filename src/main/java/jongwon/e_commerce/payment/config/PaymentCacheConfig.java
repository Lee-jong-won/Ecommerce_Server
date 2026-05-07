package jongwon.e_commerce.payment.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jongwon.e_commerce.payment.controller.response.PayApproveResponse;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PaymentCacheConfig {

    @Bean
    public Cache<PayApproveAttempt, PayApproveResponse> payApproveResponseCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

}
