package jongwon.e_commerce.large;


import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.controller.dto.PaySuccessResponse;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PaymentApprovalControllerTest {

    @LocalServerPort
    int port;
    RestClient restClient;
    String baseUrl;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void init(){
        restClient = RestClient.create();
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testPaymentApprovalApi(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        Member member = finishOrderData.getMember();
        System.out.println(member.getLoginId());
        Order order = finishOrderData.getOrder();


        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                order.getOrderId(), order.getTotalAmount());

        // when
        ResponseEntity<PaySuccessResponse> response = restClient.post()
                .uri(baseUrl + "/api/payment")
                .header("X-MOCK-USER-LOGINID", member.getLoginId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(attempt) // 👈 중요
                .retrieve()
                .toEntity(PaySuccessResponse.class);
        PaySuccessResponse paySuccessResponse = response.getBody();

        // then
        assertEquals(order.getTotalAmount(), paySuccessResponse.getPayAmount());
        assertEquals(PayMethod.MOBILE, paySuccessResponse.getPayMethod());
    }
}
