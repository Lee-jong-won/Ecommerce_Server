package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public Order order(Long memberId, String orderName, List<OrderItemRequest> requests){
        //주문 저장
        Order order = orderRepository.save(memberId, orderName);

        //주문 - 상품 저장
        List<OrderItem> orderItems = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            OrderItemRequest orderItemRequest = requests.get(i);

            Product product = productRepository.findById(orderItemRequest.getProductId());
            OrderItem orderItem = orderItemRepository.save(order.getOrderId(), product.getProductId(), product.getProductName(),
                    product.getProductPrice(), orderItemRequest.getStockQuantity());

            orderItems.add(orderItem);
        }

        //총 주문 금액 계산
        order.setTotalAmount(orderItems);
        
        return order;
    }
}
