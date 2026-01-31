package jongwon.e_commerce.payment.infra.toss;

import com.github.tomakehurst.wiremock.WireMockServer;
import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TossPaymentClientTest {

    WireMockServer wireMockServer;
    TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        TossPaymentClientErrorHandler tossPaymentClientErrorHandler = new TossPaymentClientErrorHandler(
                new ObjectMapper(), new TossPaymentErrorMapper()
        );

        HttpClientPolicy policy = HttpClientPolicy.builder()
                .connectionPoolPolicy(
                        ConnectionPolicy.builder().
                                connectTimeout(Timeout.ofSeconds(1))
                                        .socketTimeout(Timeout.ofSeconds(1))
                                .build()
                )
                .build();

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .requestFactory(
                        new HttpComponentsClientHttpRequestFactory(
                                HttpClientFactory.create(policy)
                        )
                )
                .defaultStatusHandler(HttpStatusCode::isError, tossPaymentClientErrorHandler)
                .build();

        tossPaymentClient = new TossPaymentClient(
                restClient,
                new ObjectMapper(),
                new TossPaymentErrorMapper()
        );
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void 결제승인_성공() {
        //given
        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "method": "CARD",
                          "requestedAt": "2024-01-17T12:00:02+09:00",
                          "approvedAt": "2024-01-17T12:00:00+09:00"
                        }
                    """)
                ));

        TossPaymentApproveRequest request =
                new TossPaymentApproveRequest(
                        "abcd",
                        "paykey1234",
                        "축구화외 1건",
                        "idem-key",
                        5000
                );

        //when
        TossPaymentApproveResponse response = tossPaymentClient.approve(request);

        // then
        assertThat(response.getMethod()).isEqualTo("CARD");
        assertThat(response.getRequestedAt())
                .isEqualTo(OffsetDateTime.parse("2024-01-17T12:00:02+09:00"));
        assertThat(response.getApprovedAt())
                .isEqualTo(OffsetDateTime.parse("2024-01-17T12:00:00+09:00"));
    }










}