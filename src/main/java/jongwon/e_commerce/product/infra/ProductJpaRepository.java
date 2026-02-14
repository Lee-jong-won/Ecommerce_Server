package jongwon.e_commerce.product.infra;

import jongwon.e_commerce.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
