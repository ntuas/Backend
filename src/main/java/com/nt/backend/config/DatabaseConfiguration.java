package com.nt.backend.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ConfigurationProperties("backend.datasource")
@Setter
@Slf4j
public class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private String driverClassName;
    private String url;

    @Bean
    public PoolProperties poolProperties() {
        PoolProperties poolProperties = new PoolProperties();


        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setDriverClassName(driverClassName);
            setConnectionPropertiesFromUrl(poolProperties);
        log.info(poolProperties.toString());
        return poolProperties;
    }

    private void setConnectionPropertiesFromUrl(PoolProperties poolProperties) {
        try {
            log.info("Create connection pool from given url '" + url + "'");

            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            if(userInfo != null) {
                int seperatorIndex = userInfo.indexOf(":");
                String user = userInfo.substring(0, seperatorIndex);
                String pw = userInfo.substring(seperatorIndex + 1);
                poolProperties.setUsername(user);
                poolProperties.setPassword(pw);
            }
            if(!uri.getScheme().toLowerCase().startsWith("jdbc")) {
                StringBuilder url = new StringBuilder().append("jdbc:").append(uri.getScheme()).append("://").append(uri.getHost()).append(uri.getPath());
                String query = uri.getQuery();
                if (query != null)
                    url.append("?").append(query);
                poolProperties.setUrl(url.toString());
            }
            else
                poolProperties.setUrl(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public DataSource dataSource(PoolProperties poolProperties) {

        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);

        LOGGER.info("Created new datasource: " + dataSource);
        return dataSource;
    }
}
