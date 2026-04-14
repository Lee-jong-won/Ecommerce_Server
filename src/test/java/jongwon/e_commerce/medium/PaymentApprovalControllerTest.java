package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.external.PayApprovalExecutor;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.result.fail.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.result.success.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.result.unknown.ReadTimeout;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

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
    PayApprovalExecutor payApprovalExecutor;

    @Test
    void 사용자에게_결제성공이_일어날_수_있다() throws Exception {
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", finishOrderData.getOrder().getTotalAmount());
        PayApproveSuccess payApproveSuccess = new PayApproveSuccess(PayResult.builder().
                payResultCommon(PayResult.PayResultCommon.builder().
                        payMethod(PayMethod.MOBILE).
                        approvedAt(OffsetDateTime.now()).
                        build()).
                paymentDetail(MPPay.builder().
                        customerMobilePhone("010-1234-5678").
                        settlementStatus("DONE").
                        receiptUrl("naver")
                        .build()).
                build());
        when(payApprovalExecutor.executePayApprove(any())).thenReturn(payApproveSuccess);

        // when && then
        mockMvc.perform(
                post("/api/payment")
                        .header("X-MOCK-USER-LOGINID", "testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payStatus").value("COMPLETE"))
                .andExpect(jsonPath("$.payMethod").value("MOBILE"))
                .andExpect(jsonPath("$.payAmount").isNumber());
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
                "ORDER-DEFAULT", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayApproveFail payApproveFail = new PayApproveFail("INVALID_CARD",
                "정지된 카드입니다.", HttpStatus.BAD_REQUEST );
        when(payApprovalExecutor.executePayApprove(any())).thenReturn(payApproveFail);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.payStatus").value("FAILED"))
                .andExpect(jsonPath("$.code").value("INVALID_CARD"))
                .andExpect(jsonPath("$.message").value("정지된 카드입니다."));
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
                "ORDER-DEFAULT", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        ReadTimeout readTimeout = new ReadTimeout();
        when(payApprovalExecutor.executePayApprove(any())).thenReturn(readTimeout);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.payStatus").value("TIME_OUT"))
                .andExpect(jsonPath("$.code").value("PAYMENT_TIMEOUT"))
                .andExpect(jsonPath("$.message").value("결제 시도가 많습니다. 다시 시도해주세요"));
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
                "ORDER-DEFAULT", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayApproveFail payApproveFail = new PayApproveFail("TOO_MANY_REQUEST",
                "요청이 너무 많습니다", HttpStatus.TOO_MANY_REQUESTS );
        when(payApprovalExecutor.executePayApprove(any())).thenReturn(payApproveFail);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.payStatus").value("FAILED"))
                .andExpect(jsonPath("$.code").value("TOO_MANY_REQUEST"))
                .andExpect(jsonPath("$.message").value("요청이 너무 많습니다"));
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
                "ORDER-DEFAULT", finishOrderData.getOrder().getTotalAmount());

        // 외부 API 호출 결과
        PayApproveFail payApproveFail = new PayApproveFail("CONNECTION_TIMEOUT",
                "일시적인 네트워크 오류가 발생했습니다. 다시 시도해주세요", HttpStatus.GATEWAY_TIMEOUT);
        when(payApprovalExecutor.executePayApprove(any())).thenReturn(payApproveFail);

        // when && then
        mockMvc.perform(
                        post("/api/payment")
                                .header("X-MOCK-USER-LOGINID", "testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attempt)))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.payStatus").value("PENDING"))
                .andExpect(jsonPath("$.code").value("CONNECTION_TIMEOUT"))
                .andExpect(jsonPath("$.message").value("일시적인 네트워크 오류가 발생했습니다. 다시 시도해주세요"));
    }
}
