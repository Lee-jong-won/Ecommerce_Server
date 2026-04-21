package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
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
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;

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
