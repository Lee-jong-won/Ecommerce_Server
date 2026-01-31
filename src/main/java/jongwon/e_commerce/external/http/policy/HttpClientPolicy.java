package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HttpClientPolicy {
    private RetryPolicy retryPolicy;
    private ConnectionPolicy connectionPoolPolicy;
    private RequestConfigPolicy requestConfigPolicy;
}
