package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.exception.PayClientException;
import jongwon.e_commerce.payment.exception.PayErrorCode;
import jongwon.e_commerce.payment.exception.PayServerException;
import jongwon.e_commerce.payment.exception.PayTimeoutException;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class PaymentApprovalControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    private PaymentExecutor mockExecutor;

    @Test
    void 사용자에게_결제성공이_일어날_수_있다() throws Exception {
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);

        Order order = finishOrderData.getOrder();
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                order.getOrderId(), "TOSS", order.getTotalAmount());

        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayResult payResult = PayResult.builder().
                payResultCommon(PayResult.PayResultCommon.builder().
                        orderName(order.getOrderName()).
                        payMethod(PayMethod.MOBILE).
                        approvedAt(approvedAt).
                        payMethod(PayMethod.MOBILE).
                        amount(order.getTotalAmount()).
                        build()).
                paymentDetail(Map.of("phoneNumber", "010-1234-5678",
                        "settlementStatus", "DONE",
                        "receiptUrl", "https://naver.com")).
                build();
        when(mockExecutor.supports(PGType.TOSS)).thenReturn(true);
        when(mockExecutor.executePayApprove(any())).thenReturn(payResult);

        // when && then
        mockMvc.perform(
                post("/api/payment")
                        .header("X-MOCK-USER-LOGINID", "testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payAmount").value(order.getTotalAmount()))
                .andExpect(jsonPath("$.orderName").value(order.getOrderName()))
                .andExpect(jsonPath("$.approvedAt").value(approvedAt.toString()));
    }

    @Test
    void 사용자에게_결재실패가_일어날수있다() throws Exception {
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayClientException payClientException = new PayClientException(PayErrorCode.INVALID_CARD);
        when(mockExecutor.supports(PGType.TOSS)).thenReturn(true);
        when(mockExecutor.executePayApprove(any())).thenThrow(payClientException);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CARD"))
                .andExpect(jsonPath("$.message").value("카드 정보가 잘못 되었습니다"));
    }

    @Test
    void PG에서_응답이_늦게와서_Read타임아웃이_일어날_수_있다() throws Exception {
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayTimeoutException payTimeoutException = new PayTimeoutException();
        when(mockExecutor.supports(PGType.TOSS)).thenReturn(true);
        when(mockExecutor.executePayApprove(any())).thenThrow(payTimeoutException);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.code").value("NETWORK_ERROR"))
                .andExpect(jsonPath("$.message").value("일시적인 네트워크 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }

    @Test
    void 서버에_요청이_몰려서_RequestConnectionTimeout이_발생할_수_있다() throws Exception {
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayServerException payServerException = new PayServerException("커넥션 풀이 고갈되었습니다.");

        when(mockExecutor.supports(PGType.TOSS)).thenReturn(true);
        when(mockExecutor.executePayApprove(any())).thenThrow(payServerException);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("서버 리소스가 부족합니다. 잠시 후 다시 시도해 주세요."));
    }

    @Test
    void PG서버와_연결이_안돼서_Conn타임아웃이_일어날_수_있다() throws Exception {
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayServerException payServerException = new PayServerException("연결 타임아웃 발생");
        when(mockExecutor.supports(PGType.TOSS)).thenReturn(true);
        when(mockExecutor.executePayApprove(any())).thenThrow(payServerException);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.code").value("NETWORK_ERROR"))
                .andExpect(jsonPath("$.message").value("일시적인 네트워크 오류입니다"));
    }
}
