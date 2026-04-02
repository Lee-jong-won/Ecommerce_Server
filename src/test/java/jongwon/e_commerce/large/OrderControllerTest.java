package jongwon.e_commerce.large;


import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.controller.OrderResponse;
import jongwon.e_commerce.order.domain.OrderCreate;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.PrepareOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @LocalServerPort
    int port;
    RestClient restClient;
    String baseUrl;
    @BeforeEach
    void init(){
        restClient = RestClient.create();
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testOrderApi(){
        // given
        PrepareOrderData prepareOrderData = TestDataFactory.prepareOrder(memberRepository, productRepository);
        OrderCreate orderCreate = new OrderCreate("order1", "test-order-id", prepareOrderData.getOrderItemCreates());

        // when
        ResponseEntity<OrderResponse> response = restClient.post()
                .uri(baseUrl + "/api/order")
                .header("X-MOCK-USER-LOGINID", "testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderCreate) // 👈 중요
                .retrieve()
                .toEntity(OrderResponse.class);

        // then
        OrderResponse orderResponse = response.getBody();
        assertThat(orderResponse.getOrderId()).isEqualTo("test-order-id");
        assertThat(orderResponse.getTotalAmount()).isEqualTo(15000);
    }

}
