package com.example.bai2_ss11.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.order-events:storex-order-events}")
    private String orderEventsTopic;

    @Value("${app.kafka.topic.order-events.partitions:5}")
    private int partitions;

    @Value("${app.kafka.topic.order-events.replication-factor:1}")
    private short replicationFactor;

    @Bean
    public NewTopic storexOrderEventsTopic() {
        return TopicBuilder.name(orderEventsTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}
