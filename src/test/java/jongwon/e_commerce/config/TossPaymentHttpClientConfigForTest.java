package jongwon.e_commerce.config;

import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.external.http.policy.RetryPolicy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.List;

public class TossPaymentHttpClientConfigForTest {
    public HttpClient tossHttpClientForTest(){
        return HttpClientFactory.create(
                List.of(
                        RetryPolicy.builder().disableAutomaticRetries(true)
                                .build(),
                        ConnectionPolicy.builder().socketTimeout(Timeout.ofSeconds(1))
                                .build()
                )
        );
    }

    public RestClient createTossRestClientForTest(
             HttpClient tossHttpClientForTest
    ) {
        return RestClient.builder()
                .baseUrl("http://localhost:" + 8081)
                .requestFactory(new HttpComponentsClientHttpRequestFactory(tossHttpClientForTest))
                .build();
    }
}
