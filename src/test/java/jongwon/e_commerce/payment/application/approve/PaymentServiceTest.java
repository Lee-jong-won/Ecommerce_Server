package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.*;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PaymentService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentServiceTest {
    PaymentService paymentService;
    PaymentRepository paymentRepository;
    MemberRepository memberRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    OrderRepository orderRepository;

    @BeforeEach
    void init(){
        memberRepository = new FakeMemberRepository();
        orderRepository = new FakeOrderRepository();
        productRepository = new FakeProductRepository();
        orderItemRepository = new FakeOrderItemRepository();
        paymentRepository = new FakePaymentRepository();
        paymentService = PaymentService.builder().
                paymentRepository(paymentRepository).
                orderRepository(orderRepository).
                build();
    }

    @Test
    void 주문_검증이_정상적으로_완료된후_결제가_정상적으로_저장된다(){
        // given
        Order order = orderRepository.save(TestDataFactory.finishOrder(memberRepository, productRepository, orderItemRepository, orderRepository));
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "ORDER-DEFAULT",
                order.getTotalAmount());

        // when
        Pay pay = paymentService.preProcess(request);

        // then
        assertThat(pay.getPaymentKey()).isEqualTo("a4CWyWY5m89PNh7xJwhk1");
        assertThat(pay.getPayAmount()).isEqualTo(order.getTotalAmount());
        assertThat(pay.getOrder()).isNotNull();
        assertThat(pay.getId()).isNotNull();
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void 주문_검증이_성공하지_못하면_예외가_발생한다(){
        // given
        Order order = orderRepository.save(TestDataFactory.finishOrder(memberRepository, productRepository, orderItemRepository, orderRepository));
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "ORDER-DEFAULT",
                50000);

        // when && then
        assertThrows(InvalidAmountException.class, () -> paymentService.preProcess(request));
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

        // when
        Pay updatedPay = paymentService.updatePayResult(pay.getId(), payResultCommon);

        // then
        assertThat(updatedPay.getId()).isEqualTo(pay.getId());
        assertThat(updatedPay.getPayMethod()).isEqualTo(method);
        assertThat(updatedPay.getApprovedAt()).isEqualTo(approvedAt);
    }
}