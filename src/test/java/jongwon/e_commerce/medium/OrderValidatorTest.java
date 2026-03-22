package jongwon.e_commerce.medium;


import jongwon.e_commerce.order.application.OrderValidator;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
        @Sql(value = "/sql/order-else-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class OrderValidatorTest {

    @Autowired
    OrderValidator orderValidator;

    @Test
    void 주문이_정상적으로_검증된다(){
        // given && when
        Order order = orderValidator.validateForPayment("test-id", 55000);

        // then
        assertNotNull(order);
    }

    @Test
    void 금액이_일치하지_않을_경우_예외가_반환된다(){
        assertThrows(InvalidAmountException.class, () ->
                orderValidator.validateForPayment("test-id", 10000));
    }

}
