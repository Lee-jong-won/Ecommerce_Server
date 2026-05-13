package jongwon.e_commerce.large;


import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.controller.OrderResponse;
import jongwon.e_commerce.order.controller.Cart;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.PrepareOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        Cart cart = new Cart("order1", "testUser", prepareOrderData.getCartLineItems());

        // when
        ResponseEntity<OrderResponse> response = restClient.post()
                .uri(baseUrl + "/api/order")
                .header("X-MOCK-USER-LOGINID", "testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(cart) // 👈 중요
                .retrieve()
                .toEntity(OrderResponse.class);

        // then
        OrderResponse orderResponse = response.getBody();
        assertThat(orderResponse.getTotalAmount()).isEqualTo(15000);
    }

}
