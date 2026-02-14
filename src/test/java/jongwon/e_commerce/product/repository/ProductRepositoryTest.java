package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infra.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Test
    void 상품이_정상적으로_저장된다_JPA() {
        //given
        ProductRepository productRepository = new ProductJpaRepositoryAdapter(productJpaRepository);
        String productName = "상품1";
        int productPrice = 10000;

        //when
        Product productEntity = productRepository.save(productName, productPrice);

        //then
        assertEquals(productName, productEntity.getProductName());
        assertEquals(productPrice, productEntity.getProductPrice());
    }

    @Test
    void 상품이_정상적으로_조회된다_JPA(){
        //given
        ProductRepository productRepository = new ProductJpaRepositoryAdapter(productJpaRepository);
        String productName = "상품1";
        int productPrice = 10000;

        //when
        Long productId = productRepository.save(productName, productPrice).getProductId();
        Product findProduct = productRepository.findById(productId);

        //then
        assertEquals(productId, findProduct.getProductId());
    }

}