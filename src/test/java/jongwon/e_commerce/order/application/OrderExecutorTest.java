package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.FakeMemberRepository;
import jongwon.e_commerce.mock.fake.FakeOrderItemRepository;
import jongwon.e_commerce.mock.fake.FakeOrderRepository;
import jongwon.e_commerce.mock.fake.FakeProductRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.PrepareOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderExecutorTest {

    private OrderExecutor orderExecutor;
    private ProductRepository productRepository;
    private MemberRepository memberRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    @BeforeEach
    void init(){
        // 테스트 모듈 초기화
        memberRepository = new FakeMemberRepository();
        productRepository = new FakeProductRepository();
        orderRepository = new FakeOrderRepository();
        orderItemRepository = new FakeOrderItemRepository();

        this.orderExecutor = OrderExecutor.builder().
                productRepository(productRepository).
                orderRepository(orderRepository).
                orderItemRepository(orderItemRepository).
                build();
    }

    @Test
    void 주문이_정상적으로_진행된다(){
        // give
        PrepareOrderData prepareOrderData = TestDataFactory.prepareOrder(memberRepository, productRepository);

        // when
        Order order = orderExecutor.order(prepareOrderData.getMember(),
                "order1",
                "order1",
                prepareOrderData.getOrderItemCreates());
        orderItemRepository.findByOrder(order).size();

        // then
        assertThat(orderItemRepository.findByOrder(order).size()).isEqualTo(2);
        assertThat(order.getTotalAmount()).isEqualTo(15000);
        assertThat(order.getOrderName()).isEqualTo("order1");
    }

}