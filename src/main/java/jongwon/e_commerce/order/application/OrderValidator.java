package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.exception.EmptyOrderItemsException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNameChangedException;
import jongwon.e_commerce.product.exception.ProductPriceChangedException;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Component
public class OrderValidator {

    private final ProductRepository productRepository;
    public void validate(Order order){ validate(order, getProducts(order));}
    void validate(Order order, Map<Long, Product> products){
        if(order.getOrderItems().isEmpty())
            throw new EmptyOrderItemsException("주문 항목이 비어 있습니다");

        for(OrderItem orderItem : order.getOrderItems()){
            validateOrderItem(orderItem, products.get(orderItem.getProductId()));
        }
    }

    private void validateOrderItem(OrderItem orderItem, Product product){
        if(product.getProductPrice() != orderItem.getOrderPrice())
            throw new ProductPriceChangedException(orderItem.getOrderPrice(), product.getProductPrice());

        if(product.getProductName() != orderItem.getProductName())
            throw new ProductNameChangedException(orderItem.getProductName(), product.getProductName());
    }

    private Map<Long, Product> getProducts(Order order) {
        return productRepository.findAllById(
                order.getProductIds()).stream().
                collect(toMap(Product::getProductId, identity()));
    }
}
