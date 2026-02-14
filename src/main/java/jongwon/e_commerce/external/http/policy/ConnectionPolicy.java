package jongwon.e_commerce.external.http.policy;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;

public class ConnectionPolicy implements HttpClientPolicy {
    /**
     * 전체 최대 커넥션 수
     */
    private Integer maxTotalConnections;
    /**
     * 커넥션 타임아웃 시간 - 3 way handshake
     */
    private Timeout connectTimeout;
    /**
     * Readtimeout 시간
     */
    private Timeout socketTimeout;

    @Override
    public void apply(HttpClientBuilder builder) {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(maxTotalConnections);

        ConnectionConfig.Builder connectionConfigBuilder = ConnectionConfig.custom();
        connectionConfigBuilder.setConnectTimeout(connectTimeout);
        connectionConfigBuilder.setSocketTimeout(socketTimeout);

        manager.setDefaultConnectionConfig(connectionConfigBuilder.build());
        builder.setConnectionManager(manager);

    }
}
