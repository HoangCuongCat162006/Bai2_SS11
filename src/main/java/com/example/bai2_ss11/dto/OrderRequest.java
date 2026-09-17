package com.example.bai2_ss11.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        String orderId,
        String customerId,
        List<OrderItemRequest> items,
        BigDecimal totalAmount,
        String shippingAddress
) {
}
