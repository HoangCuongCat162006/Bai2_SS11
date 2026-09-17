package com.example.bai2_ss11.event;

import com.example.bai2_ss11.dto.OrderRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderEvent(
        @JsonProperty("eventType") String eventType,
        @JsonProperty("orderId") String orderId,
        @JsonProperty("timestamp") Instant timestamp,
        @JsonProperty("payload") OrderRequest payload
) {
    public static final String EVENT_TYPE_ORDER_CREATED = "order.created";

    public static OrderEvent createOrderCreatedEvent(String orderId, OrderRequest payload) {
        return new OrderEvent(
                EVENT_TYPE_ORDER_CREATED,
                orderId,
                Instant.now(),
                payload
        );
    }

    public String getEventType() { return eventType; }
    public String getType() { return eventType; }
    public String getOrderId() { return orderId; }
    public Instant getTimestamp() { return timestamp; }
    public OrderRequest getPayload() { return payload; }
}

