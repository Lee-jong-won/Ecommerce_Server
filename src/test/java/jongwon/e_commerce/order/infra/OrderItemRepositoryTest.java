package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.config.RepositoryTestConfig;
import jongwon.e_commerce.order.domain.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RepositoryTestConfig.class})
class OrderItemRepositoryTest {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    public void testSaveAndFindOrderItem(){

        //given
        OrderItem orderItem = OrderItem.createOrderItem(1L, 1L, "테스트1",
                1000, 10);

        //when
        orderItemRepository.save(orderItem);

        //then
        assertNotNull(orderItem.getCreatedAt());
        assertNotNull(orderItem.getUpdatedAt());
    }


}