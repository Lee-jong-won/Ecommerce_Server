package jongwon.e_commerce.payment.application.approve.result.success;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.impl.MemberMemoryRepository;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.application.StockChangerByOrder;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.impl.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.impl.OrderMemoryRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.impl.ProductMemoryRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockChangerByOrderTest {
    MemberRepository memberRepository = new MemberMemoryRepository();
    ProductRepository productRepository = new ProductMemoryRepository();
    OrderItemRepository orderItemRepository = new OrderItemMemoryRepository();
    OrderRepository orderRepository = new OrderMemoryRepository();
    OrderService orderService = new OrderService(orderRepository, orderItemRepository, productRepository);
    StockChangerByOrder stockChangerByOrder = new StockChangerByOrder(orderItemRepository,
            productRepository, orderRepository);

    @AfterEach
    public void afterEach(){
        memberRepository.clearStore();
        productRepository.clearStore();
        orderItemRepository.clearStore();
        orderRepository.clearStore();
    }

    @Test
    void 주문에_포함된_모든_상품의_재고가_차감된다() {
        // given
        Member member = memberRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");

        Product product1 = productRepository.save("상품1", 1000);
        product1.changeStock(10);
        product1.startSelling();

        Product product2 = productRepository.save("상품2", 2000);
        product2.changeStock(10);
        product2.startSelling();

        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        // when
        stockChangerByOrder.decreaseStock(order.getOrderId());

        // then
        assertEquals(8, product1.getStockQuantity());
        assertEquals(7, product2.getStockQuantity());
    }

    @Test
    void 주문에_포함된_모든_상품의_재고가_증가한다(){
        // given
        Member member = memberRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");

        Product product1 = productRepository.save("상품1", 1000);
        product1.changeStock(10);
        product1.startSelling();

        Product product2 = productRepository.save("상품2", 2000);
        product2.changeStock(10);
        product2.startSelling();

        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        // when
        stockChangerByOrder.increaseStock(order.getOrderId());

        // then
        assertEquals(12, product1.getStockQuantity());
        assertEquals(13, product2.getStockQuantity());
    }

    @Test
    void 존재하지_않는_주문_조회에_대한_재고차감_작업시도시_예외가_발생한다(){
        // when && then
        assertThrows(OrderNotExistException.class, () -> stockChangerByOrder.decreaseStock("1234"));
    }

    @Test
    void 존재하지_않는_주문_조회에_대한_재고차증가_작업시도시_예외가_발생한다(){
        // when && then
        assertThrows(OrderNotExistException.class, () -> stockChangerByOrder.increaseStock("1234"));
    }
}