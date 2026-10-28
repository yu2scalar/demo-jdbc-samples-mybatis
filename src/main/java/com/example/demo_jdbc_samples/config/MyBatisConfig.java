package com.example.demo_jdbc_samples.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MyBatisConfig {

    /**
     * The databaseId follows the database actually connected to. The SQL that differs
     * between PostgreSQL and Oracle (the upsert) has one statement per databaseId;
     * everything else (TRUNCATE TABLE, FETCH FIRST n ROWS ONLY, SELECT ... FOR UPDATE,
     * LIKE) is written once.
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider(DataSource dataSource) throws SQLException {
        Properties vendors = new Properties();
        vendors.setProperty("PostgreSQL", "postgresql");
        vendors.setProperty("Oracle", "oracle");
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        provider.setProperties(vendors);
        try (Connection connection = dataSource.getConnection()) {
            String product = connection.getMetaData().getDatabaseProductName();
            String version = connection.getMetaData().getDatabaseProductVersion();
            log.info("Database: {} {} -> databaseId {}", product, version, provider.getDatabaseId(dataSource));
        }
        return provider;
    }
}
