package jongwon.e_commerce.external.http.policy;

public record HttpClientPolicy(RetryPolicy retryPolicy,
                               ConnectionPoolPolicy connectionPoolPolicy,
                               TimeoutPolicy timeoutPolicy) {

    public static HttpClientPolicy empty(){
        return new HttpClientPolicy(null, null, null);
    }
}
