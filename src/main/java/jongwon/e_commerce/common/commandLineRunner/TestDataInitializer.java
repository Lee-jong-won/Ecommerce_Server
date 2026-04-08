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

            // 주문과 주문 아이템은 연관 관계가 중요하므로 함께 처리
            insertOrdersAndItems();
            log.info(">> Order & OrderItem 각 50,000건 삽입 완료");

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

    /**
     * 주문 및 주문 상세 삽입: 소유권 보존 (Member ID = Order ID = Product ID)
     */
    private void insertOrdersAndItems() {
        String orderSql = "INSERT INTO orders (fk_member_id, order_name, order_id, order_status, ordered_at, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_item (fk_order_id, fk_product_id, order_price, order_quantity, product_name) VALUES (?, ?, ?, ?, ?)";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (int i = 0; i < 50; i++) {
            final int offset = i * 1000;

            // 1,000개 단위 Order 배치 삽입
            jdbcTemplate.batchUpdate(orderSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    long id = (long) offset + j + 1;
                    ps.setLong(1, id);           // 소유자: Member ID와 일치
                    ps.setString(2, "orderName-" + id);
                    ps.setString(3, "orderId-" + id); // Artillery 결제 요청 시 사용
                    ps.setString(4, "ORDERED");    // 결제 대기 상태
                    ps.setTimestamp(5, now);
                    ps.setLong(6, 1000);
                }
                @Override
                public int getBatchSize() { return 1000; }
            });

            // 1,000개 단위 OrderItem 배치 삽입 (Order와 1:1 매핑)
            jdbcTemplate.batchUpdate(itemSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    long id = (long) offset + j + 1;
                    ps.setLong(1, id);    // FK: Order ID와 일치
                    ps.setLong(2, id);    // FK: Product ID와 일치
                    ps.setInt(3, 1000);   // 주문 당시 가격
                    ps.setInt(4, 1);      // 주문 수량
                    ps.setString(5, "product-" + id);
                }
                @Override
                public int getBatchSize() { return 1000; }
            });

            if ((i + 1) % 10 == 0) log.info("진행률: {}/50,000 건...", (i + 1) * 1000);
        }
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
