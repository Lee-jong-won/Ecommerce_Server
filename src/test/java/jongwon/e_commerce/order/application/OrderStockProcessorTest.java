package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.FakeMemberRepository;
import jongwon.e_commerce.mock.fake.FakeOrderItemRepository;
import jongwon.e_commerce.mock.fake.FakeOrderRepository;
import jongwon.e_commerce.mock.fake.FakeProductRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.application.GeneralItemStockService;
import jongwon.e_commerce.product.application.StockService;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStockProcessorTest {

    private OrderStockProcessor orderStockProcessor;
    private MemberRepository memberRepository;
    private ProductRepository productRepository;
    private OrderItemRepository orderItemRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void init(){
        // 테스트 모듈 초기화
        memberRepository = new FakeMemberRepository();
        productRepository = new FakeProductRepository();
        orderRepository = new FakeOrderRepository();
        orderItemRepository = new FakeOrderItemRepository();

        this.orderStockProcessor = OrderStockProcessor.builder().
                orderItemRepository(orderItemRepository).stockServices(List.of(GeneralItemStockService.
                        builder().
                        productRepository(productRepository).
                        build())).build();
    }

    @Test
    void 재고가_정상적으로_감소한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);

        // when
        List<Product> products = orderStockProcessor.deductStockOf(finishOrderData.getOrder());

        // then
        Product product1 = products.get(0);
        Product product2 = products.get(1);

        assertThat(product1.getStockQuantity()).isEqualTo(99);
        assertThat(product2.getStockQuantity()).isEqualTo(99);
    }

    @Test
    void 재고가_성공적으로_증가한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);

        // when
        List<Product> products = orderStockProcessor.restoreStockOf(finishOrderData.getOrder());

        // then
        Product product1 = products.get(0);
        Product product2 = products.get(1);

        assertThat(product1.getStockQuantity()).isEqualTo(101);
        assertThat(product2.getStockQuantity()).isEqualTo(101);
    }
}