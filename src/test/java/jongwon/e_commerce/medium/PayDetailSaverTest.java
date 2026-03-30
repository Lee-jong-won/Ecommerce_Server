package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PayDetailSaver;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.MPPayRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class PayDetailSaverTest {

    @Autowired
    PayDetailSaver payDetailSaver;
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
    @Autowired
    MPPayRepository mpPayRepository;

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
