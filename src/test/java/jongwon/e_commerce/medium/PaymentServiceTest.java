package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.PaymentService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentServiceTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void 주문이_정상적으로_검증된_후_결제_데이터가_정상적으로_생성된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository, productRepository, orderItemRepository, orderRepository);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "ORDER-DEFAULT",
                "TOSS", finishOrderData.getOrder().getTotalAmount());

        // when
        Pay pay = paymentService.preProcess(finishOrderData.getMember(), request);

        // then
        assertThat(pay.getPaymentKey()).isEqualTo("a4CWyWY5m89PNh7xJwhk1");
        assertThat(pay.getPayAmount()).isEqualTo(finishOrderData.getOrder().getTotalAmount());
        assertThat(pay.getOrder()).isNotNull();
        assertThat(pay.getId()).isNotNull();
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void 주문_검증이_성공하지_못하면_예외가_발생한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository, productRepository, orderItemRepository, orderRepository);
        Order order = finishOrderData.getOrder();
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                order.getOrderId(),
                "TOSS",50000);

        // when && then
        assertThrows(InvalidAmountException.class, () -> paymentService.preProcess(finishOrderData.getMember(), request));
    }

    @Test
    void 결제_성공후_결제_결과가_성공적으로_반영된다(){
        // given

        // 결제 전처리 완료
        Pay pay = TestDataFactory.finishPayPreProcess(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository);

        // 외부 API 응답으로 온 결제 결과
        PayMethod method = PayMethod.CARD;
        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(method).
                approvedAt(approvedAt).
                build();
        Map<String, Object> paymentDetail = Map.of("customerMobilePhone", "010-1234-5678",
                "settlementStatus", "DONE", "receiptUrl", "https://naver.com");
        PayResult payResult = PayResult.builder().
                payResultCommon(payResultCommon).
                paymentDetail(paymentDetail).build();

        // when
        Pay updatedPay = paymentService.updatePayResult(pay.getId(), payResult);

        // then
        Map<String, Object> savedPaymentDetail = updatedPay.getPaymentDetail();

        assertThat(updatedPay.getId()).isEqualTo(pay.getId());
        assertThat(updatedPay.getPayMethod()).isEqualTo(method);
        assertThat(updatedPay.getApprovedAt()).isEqualTo(approvedAt);
        assertThat(savedPaymentDetail.get("customerMobilePhone")).isEqualTo("010-1234-5678");
        assertThat(savedPaymentDetail.get("settlementStatus")).isEqualTo("DONE");
        assertThat(savedPaymentDetail.get("receiptUrl")).isEqualTo("https://naver.com");
    }

}
