package jongwon.e_commerce.IntegrationTest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration
public class TestContainerConfig {
    @Bean
    @ServiceConnection
    MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer<>("mysql:8.0")
                        .withDatabaseName("ecommerce_testdb")
                        .withUsername("test")
                        .withPassword("test")
                        .withCopyFileToContainer(
                                MountableFile.forClasspathResource("/schema.sql"),
                                "/docker-entrypoint-initdb.d/"
                        ).withReuse(true);
    }
}
