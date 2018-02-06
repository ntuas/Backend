package com.nt.backend.config;

import com.nt.backend.discovery.AddressServiceDiscovery;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

@Profile("cloud")
@Configuration
@ConfigurationProperties("backend.messaging")
@Setter
@EnableRabbit
@Slf4j
public class MessagingConfiguration {

    @Autowired
    private AddressServiceDiscovery addressServiceDiscovery;

    private String orderProductsQueue;
    private String manageProductsQueue;
    private String serviceId;
    private String user;
    private String password;
    private String vhost;
    private String url;

    @Bean
    public ConnectionFactory connectionFactory() {
        if (url != null && ! url.isEmpty())
            return connectionFactoryWithUri();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addressServiceDiscovery.getAddresses(serviceId));
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);

        return connectionFactory;
    }

    private ConnectionFactory connectionFactoryWithUri() {
        try {
            log.info("Create connection factory for '" + url + "'");
            return new CachingConnectionFactory(new URI(url));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Queue orderProductsQueue() {
        Queue queue = new Queue(orderProductsQueue, true);
        rabbitAdmin().declareQueue(queue);
        return queue;
    }

    @Bean
    public Queue manageProductsQueue() {
        Queue queue = new Queue(manageProductsQueue, true);
        rabbitAdmin().declareQueue(queue);
        return queue;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
