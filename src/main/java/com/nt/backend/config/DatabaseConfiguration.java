package com.nt.backend.config;

import com.nt.backend.discovery.AddressServiceDiscovery;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Profile("cloud")
@Configuration
@ConfigurationProperties("backend.datasource")
@Setter
@Slf4j
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
    private String url;

    @Bean
    public PoolProperties poolProperties() {
        PoolProperties poolProperties = new PoolProperties();


        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setDriverClassName(driverClassName);
        if (url != null) {
            setConnectionPropertiesFromUrl(poolProperties);
        } else
            setConnectionPropertiesFromServiceRegistry(poolProperties);
        log.info(poolProperties.toString());
        return poolProperties;
    }

    private void setConnectionPropertiesFromUrl(PoolProperties poolProperties) {
        try {
            log.info("Create connection pool from given url '" + url + "'");

            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            int seperatorIndex = userInfo.indexOf(":");
            String user = userInfo.substring(0, seperatorIndex);
            String pw = userInfo.substring(seperatorIndex + 1);
            StringBuilder url = new StringBuilder().append("jdbc:").append(uri.getScheme()).append("://").append(uri.getHost()).append(uri.getPath());
            String query = uri.getQuery();
            if (query != null)
                url.append("?").append(query);
            poolProperties.setUrl(url.toString());
            poolProperties.setUsername(user);
            poolProperties.setPassword(pw);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void setConnectionPropertiesFromServiceRegistry(PoolProperties poolProperties) {
        log.info("Create connection pool from service discovery");
        String addresses = addressServiceDiscovery.getAddresses(serviceId);
        String url = String.format(URL_FORMAT, schema, addresses, database, properties);

        poolProperties.setUrl(url);
        poolProperties.setUsername(username);
        poolProperties.setPassword(password);
    }

    @Bean
    public DataSource dataSource(PoolProperties poolProperties) {

        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);

        LOGGER.info("Created new datasource: " + dataSource);
        return dataSource;
    }
}
