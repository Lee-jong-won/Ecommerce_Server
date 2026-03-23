package jongwon.e_commerce.payment.application.approve.preprocess;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.mock.fake.FakeOrderRepository;
import jongwon.e_commerce.mock.fake.FakePaymentRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayPreprocessorTest {
    PayPreprocessor payPreprocessor;
    OrderValidator orderValidator;
    PaymentRepository paymentRepository;
    OrderRepository orderRepository;

    @BeforeEach
    void init(){
        orderRepository = new FakeOrderRepository();
        orderRepository.save(createOrder());

        paymentRepository = new FakePaymentRepository();
        orderValidator = new OrderValidator(orderRepository);

        payPreprocessor = PayPreprocessor.builder()
                .orderValidator(orderValidator)
                        .paymentRepository(paymentRepository).
                build();
    }

    @Test
    void 주문_검증이_정상적으로_완료된후_결제가_정상적으로_저장된다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "order-1",
                100000);

        // when
        Pay pay = payPreprocessor.preProcess(request);

        // then
        assertThat(pay.getPaymentKey()).isEqualTo("a4CWyWY5m89PNh7xJwhk1");
        assertThat(pay.getPayAmount()).isEqualTo(100000);
        assertThat(pay.getOrder()).isNotNull();
        assertThat(pay.getId()).isEqualTo(1L);
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void 주문_검증이_성공하지_못하면_예외가_발생한다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "order-1",
                50000);

        // when && then
        assertThrows(InvalidAmountException.class, () -> payPreprocessor.preProcess(request));
    }

    private Order createOrder() {
        Member member = Member.from(
                MemberCreate.builder()
                        .loginId("testUser")
                        .password("1234")
                        .memberName("홍길동")
                        .email("test@test.com")
                        .addr("서울")
                        .build()
        );

        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        OrderItem item = OrderItem.from(product, 1);

        Order order = Order.from(
                member,
                LocalDateTime.now(),
                "order-1",
                List.of(item),
                "테스트 주문"
        );
        return order;
    }
}