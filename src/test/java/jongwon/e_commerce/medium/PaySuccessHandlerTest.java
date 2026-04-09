package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.handler.PaySuccessHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaySuccessHandlerTest {

    @Autowired
    PaySuccessHandler paySuccessHandler;

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
    void 결제_성공_핸들러가_성공적으로_동작한다(){
        // given
        Pay pay = TestDataFactory.finishPayPreProcess(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository);

        // 결제 성공 결과
        PayApproveSuccess payApproveSuccess = new PayApproveSuccess(
                PayResult.builder().
                    payResultCommon(
                        PayResult.PayResultCommon.builder().
                                payMethod(PayMethod.MOBILE).
                                approvedAt(OffsetDateTime.now()).
                                build()).
                    paymentDetail(
                        MPPay.builder().
                                customerMobilePhone("010-1234-5678").
                                settlementStatus("DONE").
                                receiptUrl("naver").
                                build()
                        ).build());


        // when
        PaySuccessResponse response = (PaySuccessResponse) paySuccessHandler.handle(pay, payApproveSuccess);

        // then
        assertThat(response.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
        assertThat(response.getPayAmount()).isEqualTo(pay.getPayAmount());
        assertThat(response.getPayMethod()).isEqualTo(PayMethod.MOBILE);
    }

}