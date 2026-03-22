package jongwon.e_commerce.medium;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
@Sql(value = "/sql/order-else-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
@Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class OrderStockProcessorTest {

    @Autowired
    OrderStockProcessor orderStockProcessor;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 재고가_성공적으로_감소된다(){
        // given
        Order order = orderRepository.getById(1L);

        // when
        orderStockProcessor.deductStockOf(order);

        // then
        Product product1 = productRepository.getById(1L);
        Product product2 = productRepository.getById(2L);

        assertThat(product1.getStockQuantity()).isEqualTo(9);
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

        assertThat(product1.getStockQuantity()).isEqualTo(11);
        assertThat(product2.getStockQuantity()).isEqualTo(11);
    }

}
