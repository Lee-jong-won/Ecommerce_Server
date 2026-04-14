package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.handler.PayFailHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.result.fail.PayApproveFail;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PayFailHandlerTest {

    @Autowired
    PayFailHandler payFailHandler;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 결제_실패가_성공적으로_반영된다(){
        // given
        Pay pay = TestDataFactory.finishPayPreProcess(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository
        );
        PayApproveFail payApproveFail = new PayApproveFail(
                "INVALID_CARD",
                "카드 정보 오류",
                HttpStatus.BAD_REQUEST
        );

        // when
        PayFailureResponse payFailureResponse = (PayFailureResponse) payFailHandler.handle(pay, payApproveFail);

        // then
        assertThat(payFailureResponse.getCode()).isEqualTo("INVALID_CARD");
        assertThat(payFailureResponse.getMessage()).isEqualTo("카드 정보 오류");
        assertThat(payFailureResponse.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

}