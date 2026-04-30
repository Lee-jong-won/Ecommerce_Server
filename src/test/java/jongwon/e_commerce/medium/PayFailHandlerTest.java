package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.handler.PayFailHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.*;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    Pay pay;

    @BeforeEach
    void init(){
        pay = TestDataFactory.finishPayPreProcess(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository
        );
    }

    @Test
    void InvalidCard는_결제실패로_처리한다(){
        // given
        InvalidCard invalidCard = new InvalidCard();

        // when
        payFailHandler.handle(pay, invalidCard);

        // then
        Pay updatedPay = paymentRepository.getById(pay.getId());
        assertThat(updatedPay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void InsufficientBalance는_결제실패로_처리한다(){
        // given
        InsufficientBalance insufficientBalance = new InsufficientBalance();

        // when
        payFailHandler.handle(pay, insufficientBalance);

        // then
        Pay updatedPay = paymentRepository.getById(pay.getId());
        assertThat(updatedPay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void InvalidErrorResponse는_결제실패로_처리한다(){
        // given
        InsufficientBalance insufficientBalance = new InsufficientBalance();

        // when
        payFailHandler.handle(pay, insufficientBalance);

        // then
        Pay updatedPay = paymentRepository.getById(pay.getId());
        assertThat(updatedPay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void JsonParsingError는_결제실패로_처리한다(){
        // given
        JsonParsingError jsonParsingError = new JsonParsingError();

        // when
        payFailHandler.handle(pay, jsonParsingError);

        // then
        Pay updatedPay = paymentRepository.getById(pay.getId());
        assertThat(updatedPay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void UnknownErrorCode는_결제실패로_처리한다(){
        // given
        UnknownErrorCode unknownErrorCode = new UnknownErrorCode();

        // when
        payFailHandler.handle(pay, unknownErrorCode);

        // then
        Pay updatedPay = paymentRepository.getById(pay.getId());
        assertThat(updatedPay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

}