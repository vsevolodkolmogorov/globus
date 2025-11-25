package ru.globus.globusproject.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private static final Logger log = Logger.getLogger(EventPublisher.class.getName());
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void publish(String topic, Long transactionId, Object payload) {
        kafkaTemplate.send(topic, transactionId, payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent event with transactionId " + transactionId + " to topic " +  topic);
                    } else {
                        log.warning("Failed to send event for transactionId " + transactionId + " " + ex);
                    }
                });
    }
}
