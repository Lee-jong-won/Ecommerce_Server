package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.adapter.OrderItemJpaRepositoryAdapter;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderItemRepositoryTest {

    @Autowired
    OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Test
    void 주문_상품이_정상적으로_저장된다_JPA(){
        // given
        OrderItemRepository orderItemRepository = new OrderItemJpaRepositoryAdapter(orderItemJpaRepository);

        Member member = Member.create("1234", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        memberJpaRepository.save(member);

        Order order = Order.createOrder(member.getMemberId(), "주문1");
        orderJpaRepository.save(order);

        Product product = Product.create("상품1", 10000);
        product.changeStock(5);
        productJpaRepository.save(product);

        // when
        OrderItem orderItemEntity = orderItemRepository.save(order.getOrderId(), product.getProductId(), product.getProductName(),
                product.getProductPrice(), product.getStockQuantity());
        OrderItem findOrderItemEntity = orderItemJpaRepository.findById(orderItemEntity.getOrderItemId()).orElseThrow();

        // then
        assertEquals(orderItemEntity.getOrderItemId(), findOrderItemEntity.getOrderItemId());
        assertEquals(orderItemEntity.getOrderId(), findOrderItemEntity.getOrderId());
        assertEquals(orderItemEntity.getOrderQuantity(), findOrderItemEntity.getOrderQuantity());
        assertEquals(orderItemEntity.getProductName(), findOrderItemEntity.getProductName());
        assertEquals(orderItemEntity.getOrderPrice(), findOrderItemEntity.getOrderPrice());
        assertEquals(orderItemEntity.getProductId(), findOrderItemEntity.getProductId());
    }



}