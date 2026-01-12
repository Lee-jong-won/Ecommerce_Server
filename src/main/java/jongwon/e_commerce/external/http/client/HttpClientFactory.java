package jongwon.e_commerce.external.http.client;

import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
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
import org.apache.hc.core5.util.Timeout;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HttpClientFactory {
    public HttpClient create(HttpClientPolicy policy) {
        HttpClientBuilder builder = HttpClients.custom();

        if (policy == null) {
            return builder.build(); // 완전 기본 설정
        }

        applyTimeout(builder, policy.getTimeoutPolicy());
        applyRetry(builder, policy.getRetryPolicy());
        applyConnectionPool(builder, policy.getConnectionPoolPolicy());

        return builder.build();
    }

    private void applyTimeout(HttpClientBuilder custom, TimeoutPolicy policy) {
        if (policy == null) return;

        RequestConfig.Builder builder = RequestConfig.custom();

        Optional.ofNullable(policy.getResponseTimeoutSeconds())
                .ifPresent(v ->
                        builder.setResponseTimeout(Timeout.ofSeconds(v))
                );

        Optional.ofNullable(policy.getConnectionRequestTimeoutSeconds())
                .ifPresent(v ->
                        builder.setConnectionRequestTimeout(Timeout.ofSeconds(v))
                );

        custom.setDefaultRequestConfig(builder.build());
    }

    private void applyRetry(HttpClientBuilder custom, RetryPolicy policy) {
        if (policy == null) return;

        if(policy.isDisableAutomaticRetries()) {
            custom.disableAutomaticRetries();
            return;
        }

        if (policy.getMaxRetries() != null && policy.getRetryIntervalSeconds() != null) {
            custom.setRetryStrategy(
                    new DefaultHttpRequestRetryStrategy(
                            policy.getMaxRetries(),
                            TimeValue.ofSeconds(policy.getRetryIntervalSeconds())
                    )
            );
        }
    }

    private void applyConnectionPool(HttpClientBuilder custom, ConnectionPolicy policy) {
        if (policy == null) return;

        PoolingHttpClientConnectionManager manager =
                new PoolingHttpClientConnectionManager();

        Optional.ofNullable(policy.getMaxTotalConnections())
                .ifPresent(manager::setMaxTotal);

        Optional.ofNullable(policy.getMaxConnectionsPerRoute())
                .ifPresent(manager::setDefaultMaxPerRoute);

        custom.setConnectionManager(manager);
    }
}
