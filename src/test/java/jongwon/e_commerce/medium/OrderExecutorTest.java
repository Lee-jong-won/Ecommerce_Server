package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.PrepareOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderExecutorTest {

    @Autowired
    OrderExecutor orderExecutor;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;

    @Test
    void 주문이_정상적으로_완료된다(){

        // given
        PrepareOrderData prepareOrderData = TestDataFactory.prepareOrder(memberRepository, productRepository);

        // when
        Order order = orderExecutor.order(prepareOrderData.getMember(), "테스트 주문", "order-Id", prepareOrderData.getOrderItemCreates());

        // then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getMember()).isNotNull();
        assertThat(order.getOrderId()).isEqualTo("order-Id");
        assertThat(order.getOrderName()).isEqualTo("테스트 주문");
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(order.getTotalAmount()).isEqualTo(15000);
    }

}
