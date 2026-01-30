package jongwon.e_commerce.order.domain;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class OrderIdGenerator {

    private static final String PREFIX = "ORD";
    private static final int RANDOM_LENGTH = 8;
    private static final String CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generate() {
        return PREFIX + "_" +
                LocalDateTime.now().format(FORMATTER) +
                "_" +
                randomString(RANDOM_LENGTH);
    }

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    // 인스턴스화 방지 (의도 명확화)
    private OrderIdGenerator() {}

}
