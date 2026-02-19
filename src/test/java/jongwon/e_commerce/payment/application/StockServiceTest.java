package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberMemoryRepository;
import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockServiceTest {
    MemberMemoryRepository memberMemoryRepository = new MemberMemoryRepository();
    ProductMemoryRepository productMemoryRepository = new ProductMemoryRepository();
    OrderItemMemoryRepository orderItemMemoryRepository = new OrderItemMemoryRepository();
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    OrderService orderService = new OrderService(orderMemoryRepository, orderItemMemoryRepository, productMemoryRepository);
    StockService stockService = new StockService(orderItemMemoryRepository,
            productMemoryRepository, orderMemoryRepository);

    @AfterEach
    public void afterEach(){
        memberMemoryRepository.clearStore();
        productMemoryRepository.clearStore();
        orderItemMemoryRepository.clearStore();
        orderMemoryRepository.clearStore();
    }

    @Test
    void 주문에_포함된_모든_상품의_재고가_차감된다() {
        // given
        Member member = memberMemoryRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");

        Product product1 = productMemoryRepository.save("상품1", 1000);
        product1.changeStock(10);
        product1.startSelling();

        Product product2 = productMemoryRepository.save("상품2", 2000);
        product2.changeStock(10);
        product2.startSelling();

        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        // when
        stockService.decreaseStock(order.getOrderId());

        // then
        assertEquals(8, product1.getStockQuantity());
        assertEquals(7, product2.getStockQuantity());
    }

    @Test
    void 주문에_포함된_모든_상품의_재고가_증가한다(){
        // given
        Member member = memberMemoryRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");

        Product product1 = productMemoryRepository.save("상품1", 1000);
        product1.changeStock(10);
        product1.startSelling();

        Product product2 = productMemoryRepository.save("상품2", 2000);
        product2.changeStock(10);
        product2.startSelling();

        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        // when
        stockService.increaseStock(order.getOrderId());

        // then
        assertEquals(12, product1.getStockQuantity());
        assertEquals(13, product2.getStockQuantity());
    }

    @Test
    void 존재하지_않는_주문_조회에_대한_재고차감_작업시도시_예외가_발생한다(){
        // when && then
        assertThrows(OrderNotExistException.class, () -> stockService.decreaseStock("1234"));
    }

    @Test
    void 존재하지_않는_주문_조회에_대한_재고차증가_작업시도시_예외가_발생한다(){
        // when && then
        assertThrows(OrderNotExistException.class, () -> stockService.increaseStock("1234"));
    }
}