package com.example.bai2_ss11.event;

import com.example.bai2_ss11.dto.OrderRequest;

import java.time.Instant;

public record OrderEvent(
        String eventType,
        String orderId,
        Instant timestamp,
        OrderRequest payload
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
}
