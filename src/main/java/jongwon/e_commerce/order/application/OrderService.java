package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public Order order(Member member, String orderName, List<OrderItemRequest> requests){
        //주문 - 상품 만들기
        List<OrderItem> orderItems = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            OrderItemRequest orderItemRequest = requests.get(i);

            Product product = productRepository.findById(orderItemRequest.getProductId()).orElseThrow(
                    () -> new ProductNotFoundException(orderItemRequest.getProductId())
            );

            OrderItem orderItem = OrderItem.createOrderItem(product,
                    product.getProductName(),
                    product.getProductPrice(),
                    orderItemRequest.getStockQuantity());

            orderItems.add(orderItem);
        }

        // 주문 생성
        Order order = Order.createOrder(member, orderName, LocalDateTime.now(), orderItems);
        orderRepository.save(order);
        for(OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);
        }
        return order;
    }
}
