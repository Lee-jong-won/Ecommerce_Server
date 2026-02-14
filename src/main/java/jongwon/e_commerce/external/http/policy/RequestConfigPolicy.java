package jongwon.e_commerce.external.http.policy;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.Timeout;

public class RequestConfigPolicy implements HttpClientPolicy {
    /**
     * 서버 응답 대기 타임아웃 (seconds)
     */
    private Timeout responseTimeout;

    /**
     * 커넥션 풀에서 커넥션을 얻기까지의 타임아웃 (seconds)
     */
    private Timeout connectionRequestTimeout;

    @Override
    public void apply(HttpClientBuilder builder) {
      builder.setDefaultRequestConfig(
              RequestConfig.custom().setResponseTimeout(responseTimeout)
                      .setConnectionRequestTimeout(connectionRequestTimeout)
                      .build()
      );
    }
}
