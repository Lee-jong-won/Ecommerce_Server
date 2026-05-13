package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.product.repository.ProductStockRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderStockProcessorTest {

    @Autowired
    private OrderStockProcessor orderStockProcessor;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductStockRepository productStockRepository;

    @Test
    void 재고가_정상적으로_감소한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);
        Order order = finishOrderData.getOrder();
        List<OrderItem> orderItems = order.getOrderItems();

        // when
        orderStockProcessor.deductStockOf(order);

        // then
        orderItems.forEach(item -> {
            Product product = productStockRepository.getById(item.getProduct().getProductId());
            assertThat(product.getStockQuantity())
                    .isEqualTo(item.getProduct().getStockQuantity() - item.getOrderQuantity());
        });
    }

    @Test
    void 재고가_성공적으로_증가한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);
        Order order = finishOrderData.getOrder();
        List<OrderItem> orderItems = order.getOrderItems();

        // when
        orderStockProcessor.restoreStockOf(finishOrderData.getOrder());

        // then
        orderItems.forEach(item -> {
            Product product = productStockRepository.getById(item.getProduct().getProductId());
            assertThat(product.getStockQuantity())
                    .isEqualTo(item.getProduct().getStockQuantity() + item.getOrderQuantity());
        });
    }

}
