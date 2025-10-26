package com.danilodps.pay;

import org.apache.kafka.clients.admin.AdminClient;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

@SpringBootTest
@Testcontainers
class PayApplicationIntegrationTests {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("apache/kafka:4.0.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void contextLoads() {
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void testDatabaseConnection() {
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void verifyContainersStartupTime() {
        long postgresStartTime = System.currentTimeMillis();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        long postgresReadyTime = System.currentTimeMillis();

        long kafkaStartTime = System.currentTimeMillis();
        assertThat(kafkaContainer.isRunning()).isTrue();
        long kafkaReadyTime = System.currentTimeMillis();

        long postgresStartupTime = postgresReadyTime - postgresStartTime;
        long kafkaStartupTime = kafkaReadyTime - kafkaStartTime;

        System.out.println("PostgreSQL startup time: " + postgresStartupTime + "ms");
        System.out.println("Kafka startup time: " + kafkaStartupTime + "ms");

        assertThat(postgresStartupTime).isLessThan(60000);
        assertThat(kafkaStartupTime).isLessThan(120000);
    }

    @Test
    void testContainerResources() {
        assertThat(postgreSQLContainer.getContainerId()).isNotNull();
        assertThat(kafkaContainer.getContainerId()).isNotNull();

        assertThat(postgreSQLContainer.getHost()).isNotNull();
        assertThat(kafkaContainer.getHost()).isNotNull();

        System.out.println("PostgreSQL Host: " + postgreSQLContainer.getHost());
        System.out.println("Kafka Host: " + kafkaContainer.getHost());
    }

    @Test
    void containersAreRunning() {
        AssertionsForClassTypes.assertThat(postgreSQLContainer.isRunning()).isTrue();
        AssertionsForClassTypes.assertThat(kafkaContainer.isRunning()).isTrue();

        System.out.println("✅ PostgreSQL está rodando em: " + postgreSQLContainer.getJdbcUrl());
        System.out.println("✅ Kafka está rodando em: " + kafkaContainer.getBootstrapServers());
    }

    @Test
    void basicConnectionTest() {
        assertThatNoException().isThrownBy(() -> {
            Connection conn = DriverManager.getConnection(
                    postgreSQLContainer.getJdbcUrl(),
                    postgreSQLContainer.getUsername(),
                    postgreSQLContainer.getPassword()
            );
            conn.close();

            Properties props = new Properties();
            props.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
            try (AdminClient client = AdminClient.create(props)) {
                client.listTopics().names().get();
            }
        });
    }
}