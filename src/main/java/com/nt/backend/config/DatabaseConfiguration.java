package com.nt.backend.config;

import com.nt.backend.discovery.AddressServiceDiscovery;
import lombok.Setter;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Profile("cloud")
@Configuration
@ConfigurationProperties("backend.datasource")
@Setter
public class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private static final String URL_FORMAT = "jdbc:%s://%s/%s?%s";

    @Autowired
    private AddressServiceDiscovery addressServiceDiscovery;

    private String serviceId;
    private String schema;
    private String database;
    private String properties;
    private String username;
    private String password;
    private String driverClassName;

    @Bean
    public PoolProperties poolProperties() {
        String addresses = addressServiceDiscovery.getAddresses(serviceId);
        String url = String.format(URL_FORMAT, schema, addresses, database, properties);

        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setDriverClassName(driverClassName);
        poolProperties.setUsername(username);
        poolProperties.setPassword(password);
        poolProperties.setUrl(url);
        return poolProperties;
    }

    @Bean
    public DataSource dataSource(PoolProperties poolProperties) {

        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);

        LOGGER.info("Created new datasource: " + dataSource);
        return dataSource;
    }
}
