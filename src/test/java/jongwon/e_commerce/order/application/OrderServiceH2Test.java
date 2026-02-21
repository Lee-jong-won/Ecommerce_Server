package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.dto.OrderItemRequest;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceH2Test {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderService orderService;

    @Test
    void 주문이_정상적으로_진행된다(){
        //given
        Member member = memberRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        Product product1 = productRepository.save("상품1", 1000);
        Product product2 = productRepository.save("상품2", 2000);
        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );

        //when
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        //then
        assertEquals(8000, order.getTotalAmount());
    }

    @Test
    void 존재하지_않는_상품은_주문되지_않는다(){
        //given
        Member member = memberRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        Product product1 = productRepository.save("상품1", 1000);
        Product product2 = productRepository.save("상품2", 2000);
        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(0L, 3),
                new OrderItemRequest(product2.getProductId(), 4)
        );

        //when && then
        assertThrows(ProductNotFoundException.class, () ->
                orderService.order(member.getMemberId(), "주문1", orderItemRequestList));
    }
}
