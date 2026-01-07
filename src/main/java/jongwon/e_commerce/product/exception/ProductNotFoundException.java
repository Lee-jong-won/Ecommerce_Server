package jongwon.e_commerce.product.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(Long productId) {
        super("존재하지 않는 상품입니다. product=" + productId);
    }

}
