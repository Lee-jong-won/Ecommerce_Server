package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.mock.FakeOrderRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidatorTest {

    private OrderValidator orderValidator;
    private OrderRepository orderRepository;

    @BeforeEach
    void init(){
        orderRepository = new FakeOrderRepository();
        orderValidator = new OrderValidator(orderRepository);
        orderRepository.save(createOrder());
    }

    @Test
    void 주문이_정상적으로_검증된다(){
        // given && when
        Order order = orderValidator.validateForPayment("order-1", 100000);

        // then
        assertNotNull(order);
    }

    @Test
    void 금액이_일치하지_않을_경우_예외가_반환된다(){
        assertThrows(InvalidAmountException.class, () ->
                orderValidator.validateForPayment("order-1", 10000));
    }

    private Order createOrder() {
        Member member = Member.from(
                MemberCreate.builder()
                        .loginId("testUser")
                        .password("1234")
                        .memberName("홍길동")
                        .email("test@test.com")
                        .addr("서울")
                        .build()
        );

        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        OrderItem item = OrderItem.from(product, 1);

        Order order = Order.from(
                member,
                LocalDateTime.now(),
                "order-1",
                List.of(item),
                "테스트 주문"
        );
        return order;
    }
}