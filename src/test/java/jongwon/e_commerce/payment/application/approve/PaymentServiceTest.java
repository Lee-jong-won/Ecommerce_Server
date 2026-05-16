package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.*;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class PaymentServiceTest {

    ProductRepository productRepository = new FakeProductRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    OrderRepository orderRepository = new FakeOrderRepository();
    PaymentRepository paymentRepository = new FakePaymentRepository();
    PayRequestRepository payRequestRepository = new FakePayRequestRepository();
    PaymentService paymentService = new PaymentService(paymentRepository, payRequestRepository);

    @Test
    void 정상적으로_결제_상태가_변경되고_결제_결과가_영속화된다(){
        // given
        PayRequest payRequest = TestDataFactory.finishPayPreProcess(
                memberRepository,
                productRepository,
                orderRepository,
                payRequestRepository);

        Order order = payRequest.getOrder();
        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(PayMethod.MOBILE).
                orderName(order.getOrderName()).
                amount(order.getTotalAmount()).
                approvedAt(approvedAt).
                build();

        Map<String, Object> detailMap = Map.of("mobilePhone", "010-1234-5678",
                "settlementStatus", "DONE",
                "receiptUrl", "http://naver.com");

        PayResult payResult = PayResult.builder().
                payResultCommon(payResultCommon).
                paymentDetail(detailMap).build();

        // when
        Pay pay = paymentService.reflectPaySuccessResult(payRequest, payResult);

        // then
        Map<String, Object> resultDetailMap = pay.getPaymentDetail();

        assertThat(pay.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(pay.getPayAmount()).isEqualTo(payResultCommon.getAmount());
        assertThat(pay.getApprovedAt()).isEqualTo(approvedAt);

        assertThat(resultDetailMap.get("mobilePhone")).isEqualTo("010-1234-5678");
        assertThat(resultDetailMap.get("settlementStatus")).isEqualTo("DONE");
        assertThat(resultDetailMap.get("receiptUrl")).isEqualTo("http://naver.com");
    }
}