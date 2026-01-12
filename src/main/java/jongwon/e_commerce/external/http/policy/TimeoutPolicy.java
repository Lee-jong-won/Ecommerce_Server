package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeoutPolicy {

    /**
     * 서버 응답 대기 타임아웃 (seconds)
     */
    private Integer responseTimeoutSeconds;

    /**
     * 커넥션 풀에서 커넥션을 얻기까지의 타임아웃 (seconds)
     */
    private Integer connectionRequestTimeoutSeconds;

}
