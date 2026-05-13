package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderCreator;
import jongwon.e_commerce.order.controller.Cart;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.PrepareOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderCreatorTest {

    @Autowired
    OrderCreator orderCreator;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;

    @Test
    void 주문이_정상적으로_완료된다(){

        // given
        PrepareOrderData prepareOrderData = TestDataFactory.prepareOrder(memberRepository, productRepository);

        // when
        Cart cart = Cart.builder().cartLineItems(prepareOrderData.getCartLineItems()).
                orderName("테스트 주문").
                loginId(prepareOrderData.getMember().getLoginId()).
                build();
        Order order = orderCreator.mapFrom("order-Id", cart);

        // then
        assertThat(order.getMember()).isNotNull();
        assertThat(order.getOrderId()).isEqualTo("order-Id");
        assertThat(order.getOrderName()).isEqualTo("테스트 주문");
        assertThat(order.getOrderStatus()).isNull();
        assertThat(order.getTotalAmount()).isEqualTo(15000);
    }

}
