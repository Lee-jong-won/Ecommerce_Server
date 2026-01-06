package jongwon.e_commerce.product.infra;

import jongwon.e_commerce.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
