package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PayPreprocessor;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PayPreprocessorTest {
    @Autowired
    PayPreprocessor payPreprocessor;
    @Autowired
    PayRequestRepository payRequestRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void 주문이_정상적으로_검증된_후_결제요청_데이터가_정상적으로_생성된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository, productRepository, orderRepository);
        Order order = finishOrderData.getOrder();
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                order.getOrderId(),
                "TOSS", order.getTotalAmount());

        // when
        PayRequest payRequest = payPreprocessor.preProcess(request);

        // then
        assertThat(payRequest.getPaymentKey()).isEqualTo("a4CWyWY5m89PNh7xJwhk1");
        assertThat(payRequest.getPayAmount()).isEqualTo(finishOrderData.getOrder().getTotalAmount());
        assertThat(payRequest.getOrder()).isNotNull();
        assertThat(payRequest.getId()).isNotNull();
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void 주문_검증이_성공하지_못하면_예외가_발생한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository, productRepository, orderRepository);
        Order order = finishOrderData.getOrder();
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                order.getOrderId(),
                "TOSS",50000);

        // when && then
        assertThrows(InvalidAmountException.class, () -> payPreprocessor.preProcess(request));
    }
}
