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

import java.util.List;
import java.util.Optional;

public class HttpClientFactory {
    public static HttpClient create(List<HttpClientPolicy> policies) {
        HttpClientBuilder builder = HttpClients.custom();

        for(HttpClientPolicy policy : policies)
            policy.apply(builder);

        return builder.build();
    }
}
