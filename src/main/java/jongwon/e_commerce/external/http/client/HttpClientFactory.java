package jongwon.e_commerce.external.http.client;

import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.external.http.policy.RetryPolicy;
import jongwon.e_commerce.external.http.policy.RequestConfigPolicy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class HttpClientFactory {
    public static HttpClient create(HttpClientPolicy policy) {
        HttpClientBuilder builder = HttpClients.custom();
        if (policy == null) {
            return builder.build(); // 완전 기본 설정
        }

        applyRequestConfig(builder, policy.getRequestConfigPolicy());
        applyRetryStrategy(builder, policy.getRetryPolicy());
        applyConnectionManager(builder, policy.getConnectionPoolPolicy());

        return builder.build();
    }

    private static void applyRequestConfig(HttpClientBuilder custom, RequestConfigPolicy policy) {
        if (policy == null) return;

        RequestConfig.Builder builder = RequestConfig.custom();

        Optional.ofNullable(policy.getResponseTimeoutSeconds())
                .ifPresent(v ->
                        builder.setResponseTimeout(policy.getResponseTimeoutSeconds())
                );

        Optional.ofNullable(policy.getConnectionRequestTimeoutSeconds())
                .ifPresent(v ->
                        builder.setConnectionRequestTimeout(policy.getConnectionRequestTimeoutSeconds())
                );

        custom.setDefaultRequestConfig(builder.build());
    }

    private static void applyRetryStrategy(HttpClientBuilder custom, RetryPolicy policy) {
        if (policy == null) return;

        if(policy.isDisableAutomaticRetries()) {
            custom.disableAutomaticRetries();
            return;
        }

        if (policy.getMaxRetries() != null && policy.getRetryIntervalSeconds() != null) {
            custom.setRetryStrategy(
                    new DefaultHttpRequestRetryStrategy(
                            policy.getMaxRetries(),
                            policy.getRetryIntervalSeconds())
                    );
        }
    }

    private static void applyConnectionManager(HttpClientBuilder custom, ConnectionPolicy policy) {
        if (policy == null) return;

        PoolingHttpClientConnectionManager manager =
                new PoolingHttpClientConnectionManager();

        Optional.ofNullable(policy.getMaxTotalConnections())
                .ifPresent(manager::setMaxTotal);

        Optional.ofNullable(policy.getMaxConnectionsPerRoute())
                .ifPresent(manager::setDefaultMaxPerRoute);

        ConnectionConfig connectionConfig = buildConnectionConfig(policy);
        if (connectionConfig != null) {
            manager.setDefaultConnectionConfig(connectionConfig);
        }
        custom.setConnectionManager(manager);
    }


    private static ConnectionConfig buildConnectionConfig(ConnectionPolicy policy) {
        ConnectionConfig.Builder builder = ConnectionConfig.custom();
        boolean configured = false;

        if (policy.getConnectTimeout() != null) {
            builder.setConnectTimeout(policy.getConnectTimeout());
            configured = true;
        }

        if (policy.getSocketTimeout() != null) {
            builder.setSocketTimeout(policy.getSocketTimeout());
            configured = true;
        }

        // 아무 값도 안 들어왔다면 굳이 setDefaultConnectionConfig 안 함
        return configured ? builder.build() : null;
    }
}
