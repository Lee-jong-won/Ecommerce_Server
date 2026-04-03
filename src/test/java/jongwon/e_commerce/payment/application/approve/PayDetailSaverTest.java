package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.*;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.MPPayRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PayDetailSaverTest {
    PayDetailSaver payDetailSaver;
    MemberRepository memberRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;
    MPPayRepository mpPayRepository;

    @BeforeEach
    void init(){
        memberRepository = new FakeMemberRepository();
        productRepository = new FakeProductRepository();
        orderItemRepository = new FakeOrderItemRepository();
        orderRepository = new FakeOrderRepository();
        paymentRepository = new FakePaymentRepository();
        mpPayRepository = new FakeMPPayRepository();
        payDetailSaver = PayDetailSaver.builder().
                mpPayRepository(mpPayRepository).
                build();
    }

    @Test
    void 휴대폰_결제_정보가_성공적으로_저장된다(){
        // given
        Pay pay = TestDataFactory.reflectPayCommonResultAfterCallingApi(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository);
        PaymentDetail paymentDetail = MPPay.createMPPay("010-1234-5678", "DONE", "naver");

        // when
        MPPay mpPay = (MPPay) payDetailSaver.save(pay, paymentDetail);

        // then
        assertThat(mpPay.getId()).isNotNull();
        assertThat(mpPay.getPay()).isNotNull();
        assertThat(mpPay.getReceiptUrl()).isEqualTo("naver");
        assertThat(mpPay.getCustomerMobilePhone()).isEqualTo("010-1234-5678");
        assertThat(mpPay.getSettlementStatus()).isEqualTo("DONE");
    }
}