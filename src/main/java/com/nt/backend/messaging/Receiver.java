package com.nt.backend.messaging;

import com.nt.backend.database.Product;
import com.nt.backend.database.ProductRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConfigurationProperties("backend.messaging")
@Setter
public class Receiver {

    private String orderQueue;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * This method reacts on a action and performs on basis of this action with a product, whereby the action must be provided by the message header and the product by the body.
     * </br>
     * There mus exist one header with the key 'action' and its value must be either 'put', 'pull' or 'order'.
     * </br>
     * If you specify 'order' as action, the payload is ignored; if the action is 'put' or 'pull' the payload is interpreted as product name.
     *
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${backend.messaging.orderManageQueue}", durable = "true"),
            exchange = @Exchange(value = "auto.exch", ignoreDeclarationExceptions = "true")))
    public void receiveMessage(Message message) {
        log.debug("Received <" + message + ">");
        String product = new String(message.getBody());
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        log.info("Have to " + action + " " + product);

        if ("put".equalsIgnoreCase(action))
            putProduct(product);
        else if ("pull".equalsIgnoreCase(action))
            getProduct(product);
        else if("order".equalsIgnoreCase(action)) {
            orderProducts();
        }
    }

    private void orderProducts() {
        productRepository.findAll().forEach(this::accept);
    }

    private void accept(Product product) {
        if (product.getProductItemsCount() < 2)
            order(product);
    }

    private void order(Product product) {
        Message message = MessageBuilder.withBody("Please order new product".getBytes())
                .andProperties(
                        MessagePropertiesBuilder.newInstance().setHeader("product", product.getProductName()).build())
                .build();
        log.info("Send message " + message + " to queue " + orderQueue);
        RabbitAdmin admin = new RabbitAdmin(this.rabbitTemplate.getConnectionFactory());
        admin.declareQueue(new org.springframework.amqp.core.Queue(orderQueue, true));
        rabbitTemplate.send(orderQueue, message);
    }

    private void getProduct(String productName) {
        Product product = productRepository.findOne(productName);
        if (product != null) {
            int productItemsCount = product.getProductItemsCount();
            if (productItemsCount > 0) {
                product.setProductItemsCount(productItemsCount - 1);
                productRepository.save(product);
                log.info("New count for " + productName + ": " + product.getProductItemsCount());
            }
        }
    }

    private void putProduct(String productName) {
        Product product = productRepository.findOne(productName);

        if (product == null)
            product = new Product(productName);
        else {
            product.setProductItemsCount(product.getProductItemsCount() + 1);
        }

        productRepository.save(product);
        log.info("New count for " + productName + ": " + product.getProductItemsCount());
    }
}
