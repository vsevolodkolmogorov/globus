package ru.globus.globusproject.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.globus.globusproject.dto.response.TransactionResponseDto;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    @KafkaListener(topics = "transaction.event", groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleUserCreated(TransactionResponseDto transactionResponseDto, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("Received event transactionId {} with amount {} from partition {}", transactionResponseDto.getId(), transactionResponseDto.getAmount(), partition);
    }
}
