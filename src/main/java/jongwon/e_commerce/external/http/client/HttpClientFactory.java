package jongwon.e_commerce.external.http.client;

import jongwon.e_commerce.external.http.policy.ConnectionPoolPolicy;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.external.http.policy.RetryPolicy;
import jongwon.e_commerce.external.http.policy.TimeoutPolicy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HttpClientFactory {

    public HttpClient create(HttpClientPolicy policy) {
        HttpClientBuilder builder = HttpClients.custom();

        if (policy == null) {
            return builder.build(); // 완전 기본 설정
        }

        applyTimeout(builder, policy.timeoutPolicy());
        applyRetry(builder, policy.retryPolicy());
        applyConnectionPool(builder, policy.connectionPoolPolicy());

        return builder.build();
    }

    private void applyTimeout(HttpClientBuilder builder, TimeoutPolicy policy) {
        if (policy == null) return;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(policy.connectTimeoutSeconds(), TimeUnit.SECONDS)
                .setResponseTimeout(policy.responseTimeoutSeconds(), TimeUnit.SECONDS)
                .setConnectionRequestTimeout(
                        policy.connectionRequestTimeoutSeconds(), TimeUnit.SECONDS
                )
                .build();

        builder.setDefaultRequestConfig(requestConfig);
    }

    private void applyRetry(HttpClientBuilder builder, RetryPolicy policy) {
        if (policy == null) return;

        builder.setRetryStrategy(
                new DefaultHttpRequestRetryStrategy(
                        policy.maxRetries(),
                        TimeValue.ofSeconds(policy.retryIntervalSeconds())
                )
        );
    }

    private void applyConnectionPool(HttpClientBuilder builder, ConnectionPoolPolicy policy) {
        if (policy == null) return;

        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(policy.maxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(policy.maxConnectionsPerRoute());

        builder.setConnectionManager(connectionManager);
        builder.evictIdleConnections(
                TimeValue.ofSeconds(policy.idleConnectionSeconds())
        );
    }
}
