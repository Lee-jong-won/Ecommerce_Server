package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.TimeValue;

@Builder
@Getter
public class RetryPolicy implements HttpClientPolicy {
    /**
     * 최대 재시도 횟수
     */
    private Integer maxRetries;

    /**
     * 재시도 간격 (seconds, ms, nanoseconds)
     */
    private TimeValue retryIntervalSeconds;

    /**
     * 자동 재시도 비활성화 여부
     */
    private boolean disableAutomaticRetries;


    @Override
    public void apply(HttpClientBuilder builder) {
        if(disableAutomaticRetries) {
            builder.disableAutomaticRetries();
            return;
        }

        builder.setRetryStrategy(new DefaultHttpRequestRetryStrategy(
                maxRetries, retryIntervalSeconds
        ));
    }
}
