package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest("classes = {OrderService.class}")
@Transactional
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 주문이_정상적으로_완료된다() {
        //given
        Member member = memberRepository.save("1234", "1234", "이종원", "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        Product product1 = productRepository.save("상품1", 3000);
        Product product2 = productRepository.save("상품2", 2000);
        Product product3 = productRepository.save("상품3", 4000);

        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 3), new OrderItemRequest(product2.getProductId(), 4),
                new OrderItemRequest(product3.getProductId(), 4)
        );

        //when
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        //then
        assertEquals(33000, order.getTotalAmount());
    }

    @Test
    void 존재하지_않는_상품은_주문할_수_없다() {
        //given
        Member member = memberRepository.save("1234", "1234", "이종원", "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        Product product1 = productRepository.save("상품1", 3000);
        Product product2 = productRepository.save("상품2", 2000);

        //DB에 저장되지 않는 상품의 ID를 임의의 값으로 설정
        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 3), new OrderItemRequest(product2.getProductId(), 4),
                new OrderItemRequest(999L, 4)
        );

        //when && then
        assertThrows(ProductNotFoundException.class,
                () -> orderService.order(member.getMemberId(), "주문1", orderItemRequestList));
    }

}
