package com.nt.backend.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.backend.database.Product;
import com.nt.backend.database.ProductRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Slf4j
@Setter
public class Receiver {

    @Autowired
    private org.springframework.amqp.core.Queue orderProductsQueue;

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
    @RabbitListener(queues = "#{manageProductsQueue.name}")
    public void receiveMessage(Message message) {
        log.debug("Received <" + message + ">");
        String product = new String(message.getBody());
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        log.info("Have to " + action + " " + product);

        if ("put".equalsIgnoreCase(action)) {
            putProduct(product);
        } else if ("take".equalsIgnoreCase(action)) {
            takeProduct(product);
        } else if ("order".equalsIgnoreCase(action)) {
            orderProducts();
        } else if ("count".equalsIgnoreCase(action)) {
            countProducts(new String(message.getBody()), message.getMessageProperties().getReplyTo(), message.getMessageProperties().getCorrelationId(), message.getMessageProperties().getCorrelationIdString());
        }
        throw new RuntimeException("Hello");
    }

    private void countProducts(String product, String replyTo, byte[] corelationId, String correlationIdString) {

        log.info("The response will be returned to reply-queue {} with correlationId {}", replyTo, correlationIdString);
        rabbitTemplate.send(replyTo, MessageBuilder
                .withBody(getAmountForProduct(product))
                .andProperties(MessagePropertiesBuilder.newInstance()
                        .setCorrelationIdString(correlationIdString)
                        .setCorrelationId(corelationId).build()).build());
    }

    private byte[] getAmountForProduct(String product) {
        Map<String, Integer> productCounts = new HashMap<>();

        if (product.isEmpty()) {
            productRepository.findAll().forEach(productFromDb -> productCounts.put(productFromDb.getProductName(), productFromDb.getProductItemsCount()));
        } else {
            Product productFromDb = productRepository.findOne(product);
            if (productFromDb != null)
                productCounts.put(productFromDb.getProductName(), productFromDb.getProductItemsCount());
        }

        try {
            return new ObjectMapper().writeValueAsString(productCounts).getBytes(UTF_8);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private void orderProducts() {
        productRepository.findAll().forEach(this::order);
    }

    private void order(Product product) {
        if (product.getProductItemsCount() >= 2)
            return;

        Message message = MessageBuilder.withBody("Please order new product".getBytes())
                .andProperties(
                        MessagePropertiesBuilder.newInstance().setHeader("product", product.getProductName()).build())
                .build();
        log.info("Send message " + message + " to queue " + orderProductsQueue);
        rabbitTemplate.send(orderProductsQueue.getName(), message);
    }

    private void takeProduct(String productName) {
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
