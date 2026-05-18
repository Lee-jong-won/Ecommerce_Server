package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PayRequestRepository payRequestRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PaymentService paymentService;
    @Test
    void 재고가_올바르게_감소하고_상태가_정상적으로_변경된다(){

        // given
        PayRequest payRequest =
                TestDataFactory.finishPayPreProcess(memberRepository, productRepository,orderRepository, payRequestRepository);
        Order order = payRequest.getOrder();
        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayResult payResult = PayResult.builder()
                .payResultCommon(PayResult.PayResultCommon.builder()
                        .payMethod(PayMethod.MOBILE)
                        .amount(order.getTotalAmount())
                        .approvedAt(approvedAt)
                        .orderName(order.getOrderName())
                        .build())
                .paymentDetail(Map.of("phoneNumber", "010-1234-5678",
                        "settlementStatus", "DONE",
                        "receiptUrl", "https://naver.com"))
                .build();


        // when
        Pay pay = paymentService.reflectPaySuccessResult(payRequest, payResult);

        // then
        paymentRepository.getById(pay.getId());
    }

}
