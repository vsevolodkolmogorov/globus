package ru.globus.globusproject.dto.kafka;

public enum KafkaTopics {
    TRANSACTION_EVENT("transaction.event");

    public final String name;

    KafkaTopics(String name) { this.name = name; }
}
