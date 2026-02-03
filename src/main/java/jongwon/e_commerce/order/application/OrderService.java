package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.exception.MemberNotFoundException;
import jongwon.e_commerce.member.infra.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductRepository;
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
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Long order(Long memberId, String orderName, List<OrderItemRequest> requests){
        Order order = createAndSaveOrder(memberId, orderName);

        List<Product> products = findProducts(requests);

        List<OrderItem> orderItems =
                createAndSaveOrderItems(order, products, requests);

        order.setTotalAmount(orderItems);

        return order.getOrderId();
    }

    private Order createAndSaveOrder(Long memberId, String orderName) {
        Order order = Order.createOrder(memberId, orderName); // 엔티티 책임
        orderRepository.save(order);               // 영속화 책임
        return order;
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private List<Product> findProducts(List<OrderItemRequest> orderItemRequests) {
        List<Product> products = new ArrayList<>();
        for (OrderItemRequest request : orderItemRequests) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
            products.add(product);
        }
        return products;
    }

    private List<OrderItem> createAndSaveOrderItems(
            Order order,
            List<Product> products,
            List<OrderItemRequest> requests
    ) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            OrderItemRequest request = requests.get(i);

            OrderItem orderItem = OrderItem.createOrderItem(
                    order.getOrderId(),
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductPrice(),
                    request.getStockQuantity()
            );

            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

        return orderItems;
    }
}
