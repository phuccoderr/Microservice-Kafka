package com.phuc.emailnotificationmicroservice.handler;


import com.phuc.core.ProductCreatedEvent;
import com.phuc.emailnotificationmicroservice.error.NotRetryableException;
import com.phuc.emailnotificationmicroservice.error.RetryableException;
import com.phuc.emailnotificationmicroservice.io.ProcessedEventEntity;
import com.phuc.emailnotificationmicroservice.io.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private RestTemplate restTemplate;
    private ProcessedEventRepository repository;

    public ProductCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreateEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        // if (true) throw new NotRetryableException("An error took place. No need to consume this message again!");
        LOGGER.info("Received a new event: " + productCreateEvent.getTitle());
        LOGGER.info("Message key: " + messageKey);

        //  check if this message was already processed before
        ProcessedEventEntity existingRecord = repository.findByMessageId(messageId);

        if (existingRecord != null) {
            LOGGER.info("Found a duplicate message id: {}", existingRecord.getMessageId());
            return;
        }


        String requestURL = "http://localhost:8082/response/200";
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestURL, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response from a remote service: " + response.getBody());
            }
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            throw new RetryableException(ex);
        } catch (HttpServerErrorException ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }
        // Save a unique message id in a database table
        try {
            repository.save(new ProcessedEventEntity(messageId, productCreateEvent.getProductId()));
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }
    }
}
