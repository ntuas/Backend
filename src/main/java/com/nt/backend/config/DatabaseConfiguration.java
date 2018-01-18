package com.nt.backend.config;

import com.nt.backend.discovery.AddressServiceDiscovery;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
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
    public DataSource dataSource() {
        String addresses = addressServiceDiscovery.getAddresses(serviceId);
        String url = String.format(URL_FORMAT, schema, addresses, database, properties);
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
