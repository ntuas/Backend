package com.nt.backend.test.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class MessagingTestConfiguration {


    @Bean
    public Queue orderProductsQueue() {
        return new Queue("order");
    }

    @Bean
    public Queue manageProductsQueue() {
        return new Queue("manage");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(mock(ConnectionFactory.class));
        return simpleRabbitListenerContainerFactory;
    }
}
