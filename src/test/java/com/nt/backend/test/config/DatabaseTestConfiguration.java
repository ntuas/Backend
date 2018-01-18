package com.nt.backend.test.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseTestConfiguration {

    @Bean
    @ConfigurationProperties("backend.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
