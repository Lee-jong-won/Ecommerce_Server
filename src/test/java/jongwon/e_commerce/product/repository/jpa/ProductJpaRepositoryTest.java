package jongwon.e_commerce.product.repository.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = true)
@TestPropertySource("classpath:application-test.properties")
@Sql(value = "/sql/product-repository-test-data.sql")
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Test
    void findBy_Id로_상품을_찾아올_수_있다(){
        // given && when
        Optional<ProductEntity> result = productJpaRepository.findById(1L);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findById_는_데이터가_없으면_Optional_empty를_내려준다(){
        // given && when
        Optional<ProductEntity> result = productJpaRepository.findById(2L);

        // then
        assertThat(result.isEmpty()).isTrue();
    }


}