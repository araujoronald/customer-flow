package com.github.araujoronald.infra.adapters.repositories.postgres.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionFactory {

    private static final HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("postgres.properties")) {
                if (input == null) {
                    throw new RuntimeException("Sorry, unable to find postgres.properties");
                }
                props.load(input);
            }

            HikariConfig config = new HikariConfig(props);
            config.setJdbcUrl(props.getProperty("jdbcUrl"));

            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}