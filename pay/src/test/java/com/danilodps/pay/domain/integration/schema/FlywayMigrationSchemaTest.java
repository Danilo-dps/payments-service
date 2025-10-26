package com.danilodps.pay.domain.integration.schema;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class FlywayMigrationSchemaTest {

    @Autowired
    private DataSource dataSource;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("payment_schema_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "false");
    }

    @Test
    void shouldCreateAllTablesFromMigration() throws Exception {

        List<String> tableNames = getTableNames();

        assertThat(tableNames).containsExactlyInAnyOrder(
                "tb_roles",
                "tb_users",
                "user_roles",
                "tb_store",
                "store_roles",
                "tb_deposit",
                "tb_transactions",
                "flyway_schema_history"
        );
    }

    @Test
    void shouldHaveAllPrimaryKeys() throws Exception {

        String[] tables = {"tb_roles", "tb_users", "user_roles", "tb_store", "store_roles", "tb_deposit", "tb_transactions"};

        for (String table : tables) {
            boolean hasPrimaryKey = hasPrimaryKey(table);
            assertThat(hasPrimaryKey)
                    .withFailMessage("Table %s should have a primary key", table)
                    .isTrue();
        }
    }

    @Test
    void shouldHaveAllForeignKeys() throws Exception {
        List<String> expectedFKs = List.of(
                "fk_user_roles_user_id", "fk_user_roles_role_id",
                "fk_store_roles_store_id", "fk_store_roles_role_id",
                "fk_tb_deposit_user",
                "fk_tb_transactions_sender_user", "fk_tb_transactions_receiver_user",
                "fk_tb_transactions_receiver_store"
        );

        List<String> actualFKs = getForeignKeyNames();

        assertThat(actualFKs).containsAll(expectedFKs);
    }

    @Test
    void shouldHaveAllUniqueConstraints() throws Exception {
        List<String> uniqueConstraints = getUniqueConstraints();

        assertThat(uniqueConstraints).contains(
                "uk_user_email", "uk_user_cpf",
                "uk_store_email", "uk_store_cnpj",
                "idx_tb_roles_role_name"
        );
    }

    @Test
    void shouldHaveAllIndexes() throws Exception {
        List<String> expectedIndexes = List.of(
                "idx_tb_users_email", "idx_tb_users_document",
                "idx_user_roles_user_id", "idx_user_roles_role_id",
                "idx_tb_store_email", "idx_tb_store_cnpj",
                "idx_store_roles_store_id", "idx_store_roles_role_id",
                "idx_tb_deposit_user_id", "idx_tb_deposit_timestamp", "idx_tb_deposit_operation_type",
                "idx_tb_transactions_sender_user_id", "idx_tb_transactions_receiver_user_id",
                "idx_tb_transactions_receiver_store_id", "idx_tb_transactions_timestamp",
                "idx_tb_transactions_amount"
        );

        List<String> actualIndexes = getIndexNames();

        assertThat(actualIndexes).containsAll(expectedIndexes);
    }

    @Test
    void shouldHaveDefaultRolesInserted() throws Exception {

        List<String> roles = getRoles();

        assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_STORE");
    }

    @Test
    void shouldHaveCheckConstraints() throws Exception {
        List<String> checkConstraints = getCheckConstraints();

        assertThat(checkConstraints).anyMatch(constraint ->
                constraint.contains("chk_tb_transactions_valid_receiver"));
        assertThat(checkConstraints).anyMatch(constraint ->
                constraint.contains("chk_tb_transactions_not_self_transaction"));
    }

    private List<String> getTableNames() throws Exception {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME").toLowerCase());
            }
        }
        return tableNames;
    }

    private boolean hasPrimaryKey(String tableName) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
            return primaryKeys.next();
        }
    }

    private List<String> getForeignKeyNames() throws Exception {
        List<String> fkNames = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String[] tables = {"user_roles", "store_roles", "tb_deposit", "tb_transactions"};
            for (String table : tables) {
                ResultSet foreignKeys = metaData.getImportedKeys(null, null, table);
                while (foreignKeys.next()) {
                    fkNames.add(foreignKeys.getString("FK_NAME").toLowerCase());
                }
            }
        }
        return fkNames;
    }

    private List<String> getUniqueConstraints() throws Exception {
        List<String> uniqueConstraints = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet indexes = metaData.getIndexInfo(null, null, "tb_users", true, false);
            while (indexes.next()) {
                if (!indexes.getBoolean("NON_UNIQUE")) {
                    uniqueConstraints.add(indexes.getString("INDEX_NAME").toLowerCase());
                }
            }

            indexes = metaData.getIndexInfo(null, null, "tb_store", true, false);
            while (indexes.next()) {
                if (!indexes.getBoolean("NON_UNIQUE")) {
                    uniqueConstraints.add(indexes.getString("INDEX_NAME").toLowerCase());
                }
            }

            indexes = metaData.getIndexInfo(null, null, "tb_roles", true, false);
            while (indexes.next()) {
                if (!indexes.getBoolean("NON_UNIQUE")) {
                    uniqueConstraints.add(indexes.getString("INDEX_NAME").toLowerCase());
                }
            }
        }
        return uniqueConstraints;
    }

    private List<String> getIndexNames() throws Exception {
        List<String> indexNames = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String[] tables = {"tb_users", "user_roles", "tb_store", "store_roles", "tb_deposit", "tb_transactions"};
            for (String table : tables) {
                ResultSet indexes = metaData.getIndexInfo(null, null, table, false, false);
                while (indexes.next()) {
                    String indexName = indexes.getString("INDEX_NAME");
                    if (indexName != null && !indexName.contains("pk")) {
                        indexNames.add(indexName.toLowerCase());
                    }
                }
            }
        }
        return indexNames;
    }

    private List<String> getRoles() throws Exception {
        List<String> roles = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            var statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT role_name FROM tb_roles");
            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
        }
        return roles;
    }

    private List<String> getCheckConstraints() throws Exception {
        List<String> constraints = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            // PostgreSQL specific query for check constraints
            var statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT conname FROM pg_constraint WHERE conrelid = '" + "tb_transactions" + "'::regclass AND contype = 'c'"
            );
            while (rs.next()) {
                constraints.add(rs.getString("conname").toLowerCase());
            }
        }
        return constraints;
    }

    @Test
    void debugPrintDatabaseInfo() throws Exception {
        System.out.println("=== DEBUG DATABASE INFO ===");

        List<String> tableNames = getTableNames();
        System.out.println("TABLES: " + tableNames);

        List<String> fkNames = getForeignKeyNames();
        System.out.println("FOREIGN KEYS: " + fkNames);

        List<String> indexes = getIndexNames();
        System.out.println("INDEXES: " + indexes);

        System.out.println("=== END DEBUG ===");
    }
}