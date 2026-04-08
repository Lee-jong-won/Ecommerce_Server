package jongwon.e_commerce.common.commandLineRunner;

import jakarta.persistence.EntityManager;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class TestDataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("=== JDBC Bulk Insert 시작 (Target: 각 테이블 50,000건) ===");
        // 1. 제약 조건 잠시 해제 (속도 향상 및 삽입 순서 자유도 확보)
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        try {
            // 2. 각 도메인별 벌크 삽입 실행
            insertMembers();
            log.info(">> Member 50,000건 삽입 완료");

            insertProducts();
            log.info(">> Product 50,000건 삽입 완료");
            log.info("=== 모든 테스트 데이터 준비가 완료되었습니다! ===");
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생: ", e);
            throw e;
        } finally {
            // 3. 제약 조건 반드시 복구
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    /**
     * 회원 데이터 삽입: user-1 ~ user-50000
     */
    private void insertMembers() {
        String sql = "INSERT INTO member (member_name, login_id, password, email, addr) VALUES (?, ?, ?, ?, ?)";
        executeInBatches(sql, (ps, id) -> {
            ps.setString(1, "user" + id);
            ps.setString(2, "user-" + id); // Artillery 로그인 ID로 사용
            ps.setString(3, "pass");
            ps.setString(4, "user" + id + "@test.com");
            ps.setString(5, "Seoul");
        });
    }

    /**
     * 상품 데이터 삽입: product-1 ~ product-50000
     */
    private void insertProducts() {
        String sql = "INSERT INTO product (product_name, product_price, stock_quantity, product_status) VALUES (?, ?, ?, ?)";
        executeInBatches(sql, (ps, id) -> {
            ps.setString(1, "product-" + id);
            ps.setLong(2, 1000L); // 기본 가격 1000원
            ps.setInt(3, 30000);  // 넉넉한 재고량
            ps.setString(4, "SELLING");
        });
    }

    private void executeInBatches(String sql, BatchSetter setter) {
        for (int i = 0; i < 50; i++) {
            final int offset = i * 1000;
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    setter.set(ps, offset + j + 1);
                }
                @Override
                public int getBatchSize() { return 1000; }
            });
        }
    }

    @FunctionalInterface
    interface BatchSetter {
        void set(PreparedStatement ps, int id) throws SQLException;
    }

}
