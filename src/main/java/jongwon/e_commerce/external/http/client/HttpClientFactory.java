 package jongwon.e_commerce.external.http.client;

import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.List;

public class HttpClientFactory {
    public static HttpClient create(List<HttpClientPolicy> policies) {
        HttpClientBuilder builder = HttpClients.custom();

        for(HttpClientPolicy policy : policies)
            policy.apply(builder);

        return builder.build();
    }
}
