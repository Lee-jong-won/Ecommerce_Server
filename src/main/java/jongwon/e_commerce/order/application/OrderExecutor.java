package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Builder
public class OrderExecutor {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order order(Member member, String orderName, List<OrderItemCreate> requests){
        //주문 - 상품 만들기
        List<OrderItem> orderItems = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            OrderItemCreate orderItemCreate = requests.get(i);

            long productId = orderItemCreate.getProductId();
            int stockQuantity = orderItemCreate.getStockQuantity();

            Product product = productRepository.getById(productId);
            OrderItem orderItem = OrderItem.from(product, stockQuantity);

            orderItems.add(orderItem);
        }

        // 주문 생성
        Order order = Order.from(member,
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                orderItems,
                orderName);

        // 주문과 주문 상품 저장
        Order savedOrder = orderRepository.save(order);
        for(OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);
        }

        return savedOrder;
    }
}
