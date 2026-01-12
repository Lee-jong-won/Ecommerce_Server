package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RetryPolicy {

    /**
     * 최대 재시도 횟수
     */
    private Integer maxRetries;

    /**
     * 재시도 간격 (seconds)
     */
    private Integer retryIntervalSeconds;

    /**
     * 자동 재시도 비활성화 여부
     */
    private boolean disableAutomaticRetries;

}
