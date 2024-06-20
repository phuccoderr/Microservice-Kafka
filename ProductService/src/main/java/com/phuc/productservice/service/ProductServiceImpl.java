package com.phuc.productservice.service;

import com.phuc.core.ProductCreatedEvent;
import com.phuc.productservice.controller.CreateProductRestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService{

    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) throws Exception {
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId,
                productRestModel.getTitle(),
                productRestModel.getPrice(),
                productRestModel.getQuantity());

        // Khong dong bo
//        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate.send("product-created-events-topic",
//                productId,
//                productCreatedEvent);
//
//        future.whenComplete((result,exception) -> {
//            if (exception != null) {
//                LOGGER.error("****** Failed to send message: " + exception.getMessage());
//            } else {
//                LOGGER.info("****** Message sent successfully: " + result.getRecordMetadata());
//            }
//        });
//
//        LOGGER.info("****** Returning product id");
//        // Muon Xu ly dong bo thi them join
//        future.join();


        // Dong bo
        LOGGER.info("Before publishing a ProductCreatedEvent");
        SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send("product-created-events-topic",
                productId,
                productCreatedEvent).get();
        LOGGER.info("Partition: " + result.getRecordMetadata().partition());
        LOGGER.info("Topic: " + result.getRecordMetadata().topic());
        LOGGER.info("Offset: " + result.getRecordMetadata().offset());
        LOGGER.info("****** Returning product id");

        return productId;
    }
}
