package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.mock.FakeOrderItemRepository;
import jongwon.e_commerce.mock.FakeOrderRepository;
import jongwon.e_commerce.mock.FakeProductRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStockProcessorTest {

    private OrderStockProcessor orderStockProcessor;
    private OrderExecutor orderExecutor;
    private OrderItemRepository orderItemRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void init(){
        // 테스트 모듈 초기화
        productRepository = new FakeProductRepository();
        orderRepository = new FakeOrderRepository();
        orderItemRepository = new FakeOrderItemRepository();

        this.orderExecutor = OrderExecutor.builder().
                productRepository(productRepository).
                orderRepository(orderRepository).
                orderItemRepository(orderItemRepository).
                build();

        this.orderStockProcessor = new OrderStockProcessor(orderItemRepository, productRepository);

        // 상품 저장
        Product product1 = Product.from("노트북", 20000);
        product1.changeStock(10);
        product1.setStatus(ProductStatus.SELLING);

        Product product2 = Product.from("핸드폰", 5000);
        product2.changeStock(10);
        product2.setStatus(ProductStatus.SELLING);

        productRepository.save(product1);
        productRepository.save(product2);

        Member member = Member.from(
                MemberCreate.builder()
                        .loginId("testUser")
                        .password("1234")
                        .memberName("홍길동")
                        .email("test@test.com")
                        .addr("서울")
                        .build()
        );

        OrderItemCreate orderItemCreate1 = OrderItemCreate.builder()
                .stockQuantity(2)
                .productId(1L).build();

        OrderItemCreate orderItemCreate2 = OrderItemCreate.builder()
                .stockQuantity(1)
                .productId(2L).build();

        List<OrderItemCreate> orderItemCreateList = List.of(orderItemCreate1, orderItemCreate2);
        orderExecutor.order(member, "노트북 외 1건", orderItemCreateList);
    }

    @Test
    void 재고가_정상적으로_감소한다(){
        // given
        Order order = orderRepository.getById(1L);

        // when
        orderStockProcessor.deductStockOf(order);

        // then
        Product product1 = productRepository.getById(1L);
        Product product2 = productRepository.getById(2L);
        assertThat(product1.getStockQuantity()).isEqualTo(8);
        assertThat(product2.getStockQuantity()).isEqualTo(9);
    }

    @Test
    void 재고가_성공적으로_증가한다(){
        // given
        Order order = orderRepository.getById(1L);

        // when
        orderStockProcessor.restoreStockOf(order);

        // then
        Product product1 = productRepository.getById(1L);
        Product product2 = productRepository.getById(2L);
        assertThat(product1.getStockQuantity()).isEqualTo(12);
        assertThat(product2.getStockQuantity()).isEqualTo(11);
    }


}