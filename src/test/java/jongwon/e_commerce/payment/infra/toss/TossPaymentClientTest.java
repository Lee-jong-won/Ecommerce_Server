package jongwon.e_commerce.payment.infra.toss;

import com.github.tomakehurst.wiremock.WireMockServer;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.payment.toss.TossPaymentClientErrorHandler;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class TossPaymentClientTest {
    WireMockServer wireMockServer;
    TossPaymentClientErrorHandler tossPaymentClientErrorHandler;
    HttpClientPolicy basePolicy;
    SimpleRetryPolicy basicRetryPolicy;
    BackOffPolicy basicBackOffPolicy;
    RetryTemplate retryTemplate;

    /*@BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        tossPaymentClientErrorHandler = new TossPaymentClientErrorHandler(
                new ObjectMapper(), new TossPaymentErrorMapper()
        );

        basePolicy = HttpClientPolicy.builder()
                .connectionPoolPolicy(
                        ConnectionPolicy.builder().
                                connectTimeout(Timeout.ofSeconds(1))
                                        .socketTimeout(Timeout.ofSeconds(1))
                                .build()
                )
                .build();

        // 재시도 정책: 최대 3회
        basicRetryPolicy = new SimpleRetryPolicy(
                3,
                Map.of(
                        TossPaymentRetryableException.class, true
                )
        );

        // 고정 간격 1초 대기
        basicBackOffPolicy = new FixedBackOffPolicy();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void 결제승인_성공() {
        //given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

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

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        TossPaymentApproveResponse response = client.callApproveApi(request);

        // then
        assertThat(response.getMethod()).isEqualTo("CARD");
        assertThat(response.getRequestedAt())
                .isEqualTo(OffsetDateTime.parse("2024-01-17T12:00:02+09:00"));
        assertThat(response.getApprovedAt())
                .isEqualTo(OffsetDateTime.parse("2024-01-17T12:00:00+09:00"));
    }

    @Test
    void 결제승인_중_ReadTimeout_발생시_TossApiTimeoutException_발생() {
        // given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

        wireMockServer.stubFor(
                post(urlEqualTo("/payments/confirm"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(3000) // read timeout 유도
                        )
        );

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        // when & then
        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossApiTimeoutException.class);
    }

    @Test
    void PG와_연결이_안되면_network_exception() {
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:65535",
                basePolicy,
                retryTemplate
        );

        TossPaymentApproveRequest request =
                new TossPaymentApproveRequest(
                        "abcd",
                        "paykey1234",
                        "idem-key",
                        5000
                );

        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossApiNetworkException.class);
    }

    @Test
    void 비즈니스_관련_에러_응답이_오면_TossPaymentUserFaultException(){
        //given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "code": "INVALID_REQUEST",
                          "message": "잘못된 요청입니다."
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossPaymentUserFaultException.class);
    }

    @Test
    void 서버처리_관련_에러_응답이_오면_TossRetryableException(){
        //given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "message": "너무 많은 요청이 몰렸습니다. 다시 시도해주세요"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossPaymentRetryableException.class);
    }

    @Test
    void 인증_관련_에러_응답이_오면_TossSystemException(){
        //given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "code" : "UNAUTHORIZED_KEY",
                            "message" : "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossPaymentSystemException.class);
    }

    @Test
    void API_SPEC_변화시_TossPaymentException(){
        //given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "ApiCode" : "UNAUTHORIZED_KEY",
                            "ApiMessage" : "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    void 새로운_에러코드_추가시_TossPaymentException(){
        //given
        retryTemplate = createRetryTemplate(basicRetryPolicy, basicBackOffPolicy);
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy,
                retryTemplate
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "code" : "NEW_ERROR_CODE",
                            "message" : "새로 추가된 에러코드 입니다"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.callApproveApi(request))
                .isInstanceOf(TossPaymentException.class);
    }


    private TossPaymentClient createClient(String baseUrl,
                                           HttpClientPolicy policy,
                                           RetryTemplate retryTemplate) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(
                        new HttpComponentsClientHttpRequestFactory(
                                HttpClientFactory.create(policy)
                        )
                )
                .defaultStatusHandler(HttpStatusCode::isError, tossPaymentClientErrorHandler)
                .build();

        return new TossPaymentClient(
                restClient,
                new ObjectMapper(),
                retryTemplate
        );
    }

    private RetryTemplate createRetryTemplate(SimpleRetryPolicy simpleRetryPolicy, BackOffPolicy backOffPolicy){
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(simpleRetryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }*/
}