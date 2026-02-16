package jongwon.e_commerce.product.repository.jpa;

import jongwon.e_commerce.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
