package jongwon.e_commerce.payment.infra.http;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PaymentHttpClientConfig {

    // 연결 풀(Connection Pool) 설정 값
    // private static final int MAX_TOTAL_CONNECTIONS = 200; // 최대 전체 커넥션 수
    // private static final int MAX_CONNECTIONS_PER_ROUTE = 20; // 특정 호스트(경로)별 최대 커넥션 수
    // private static final int MAX_IDLE_TIME = 30; // 유휴 연결 유지 시간 (초 단위)

    // 재시도 설정 값
    // private static final int MAX_RETRIES = 0; // 요청 실패 시 재시도 횟수
    // private static final long RETRY_INTERVAL_IN_SECONDS = 1L; // 재시도 간격 (초 단위)

    // 타임아웃 설정 값
    private static final int CONNECT_TIMEOUT_SECONDS = 1;
    private static final int RESPONSE_TIMEOUT_SECONDS = 30;
    // private static final long CONNECTION_REQUEST_TIMEOUT = 3L; // 연결 요청 타임아웃 (초 단위)


    @Bean
    @Qualifier("paymentHttpClient")
    public HttpClient paymentHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig())
                .disableAutomaticRetries() // 결제 API이므로 명시적으로 차단
                .build();
    }

    /**
     * 연결 풀(Connection Pool)을 생성하고 최대 연결 수와 라우트별 최대 연결 수를 설정
     *
     * @return PoolingHttpClientConnectionManager 객체
     */
    /*private PoolingHttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        return connectionManager;
    }*/

    /**
     * HTTP 요청의 응답 및 연결 요청 시간을 설정
     *
     * @return RequestConfig 객체
     */
    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setResponseTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .setConnectionRequestTimeout(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    /**
     * HTTP 요청 실패 시 재시도 전략을 설정
     *
     * @return DefaultHttpRequestRetryStrategy 객체
     */
    /*private DefaultHttpRequestRetryStrategy buildRetryStrategy() {
        return new DefaultHttpRequestRetryStrategy(
                MAX_RETRIES, TimeValue.ofSeconds(RETRY_INTERVAL_IN_SECONDS)
        );
    }*/


}
