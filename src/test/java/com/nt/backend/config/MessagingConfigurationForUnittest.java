package com.nt.backend.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mock;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConfigurationProperties("backend.messaging")
@Setter
@EnableRabbit
@Slf4j
@Profile("unittest")
public class MessagingConfigurationForUnittest {

    private String orderProductsQueue;
    private String manageProductsQueue;
    private String url;

    @MockBean
    ConnectionFactory connectionFactory;

    @MockBean
    RabbitAdmin rabbitAdmin;

    @Bean
    public Queue orderProductsQueue() {
        return new Queue("order");
    }

    @Bean
    public Queue manageProductsQueue() {
        return new Queue("manage");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        when(connectionFactory.createConnection()).thenReturn(mock(Connection.class));
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        return simpleRabbitListenerContainerFactory;
    }
}
