package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.mock.fake.FakeMPPayRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.MPPayRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PayDetailSaverTest {
    PayDetailSaver payDetailSaver;
    MPPayRepository mpPayRepository;
    @BeforeEach
    void init(){
        mpPayRepository = new FakeMPPayRepository();
        payDetailSaver = PayDetailSaver.builder().
                mpPayRepository(mpPayRepository).
                build();
    }

    @Test
    void 휴대폰_결제_정보가_성공적으로_저장된다(){
        // given
        Pay pay = createPay();
        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(PayMethod.MOBILE).build();
        Pay updatedPay = pay.reflectPaySuccess(payResultCommon);
        PaymentDetail paymentDetail = MPPay.from("010-1234-5678", "DONE", "naver");

        // when
        MPPay mpPay = (MPPay) payDetailSaver.save(updatedPay, paymentDetail);

        // then
        assertThat(mpPay.getId()).isEqualTo(1L);
        assertThat(mpPay.getPay().getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(mpPay.getPay().getPaymentKey()).isEqualTo("paymentKey");
        assertThat(mpPay.getReceiptUrl()).isEqualTo("naver");
        assertThat(mpPay.getCustomerMobilePhone()).isEqualTo("010-1234-5678");
        assertThat(mpPay.getSettlementStatus()).isEqualTo("DONE");
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

    private Pay createPay(){
        Order order = createOrder();
        return Pay.from(order, "paymentKey", 10000L);
    }
}