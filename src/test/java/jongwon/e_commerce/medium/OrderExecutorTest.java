package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Sql(value = "/sql/order-executor-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class OrderExecutorTest {

    @Autowired
    private OrderExecutor orderExecutor;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 주문이_정상적으로_완료된다(){

        // given
        Member member = memberRepository.getById(3L);

        OrderItemCreate orderItemCreate1 = OrderItemCreate.builder()
                .stockQuantity(2)
                .productId(3L).build();

        OrderItemCreate orderItemCreate2 = OrderItemCreate.builder()
                .stockQuantity(1)
                .productId(4L).build();

        List<OrderItemCreate> orderItemCreateList = List.of(orderItemCreate1, orderItemCreate2);

        // when
        Order order = orderExecutor.order(member, "테스트 주문", orderItemCreateList);

        // then
        assertThat(order.getOrderId()).isNotNull();
        assertThat(order.getOrderName()).isEqualTo("테스트 주문");
        assertThat(order.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(order.getTotalAmount()).isEqualTo(105000);
    }

}
