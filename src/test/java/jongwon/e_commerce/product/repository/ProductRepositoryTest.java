package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.adapter.ProductJpaRepositoryAdapter;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
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
        Product product = productRepository.save(productName, productPrice);

        //then
        assertEquals(productName, product.getProductName());
        assertEquals(productPrice, product.getProductPrice());
    }

    @Test
    void 상품이_정상적으로_조회된다_JPA(){
        //given
        ProductRepository productRepository = new ProductJpaRepositoryAdapter(productJpaRepository);
        Product product = Product.create("상품1", 10000);

        //when
        productJpaRepository.save(product);

        //then
        assertDoesNotThrow(() -> productRepository.findById(product.getProductId()));
    }
    @Test
    void 상품이_정상적으로_조회되지_않은_경우_예외_Throw_JPA(){
        //given
        ProductRepository productRepository = new ProductJpaRepositoryAdapter(productJpaRepository);

        //when && then
        assertThrows(ProductNotFoundException.class,() -> productRepository.findById(1L));
    }
}