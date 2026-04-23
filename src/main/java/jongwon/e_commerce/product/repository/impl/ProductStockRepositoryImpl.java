package jongwon.e_commerce.product.repository.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductStockRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Builder
@Repository
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Product> findById(Long productId) {
        String sql = """
        SELECT product_id, stock_quantity, product_status, version
        FROM product
        WHERE product_id = ?
    """;

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                Product.builder().
                        productId(rs.getLong("product_id")).
                        stockQuantity(rs.getInt("stock_quantity")).
                        productStatus(ProductStatus.valueOf(rs.getString("product_status"))).
                        version(rs.getInt("version")).build(), productId));
    }

    @Override
    public Product getById(Long productId) {
        return findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("product", productId)
        );
    }

    @Override
    public int updateStockAndStatus(Long productId,
                                    int stockQuantity,
                                    ProductStatus productStatus,
                                    Integer version) {

        String sql = """
        UPDATE product
        SET stock_quantity = ?, product_status = ?, version = version + 1
        WHERE product_id = ? AND version = ? AND product_status = 'SELLING'
    """;

        return jdbcTemplate.update(sql,
                stockQuantity,
                productStatus.name(),
                productId,
                version
        );
    }
}
