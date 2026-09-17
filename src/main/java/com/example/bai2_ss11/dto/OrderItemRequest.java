package com.example.bai2_ss11.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        String productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}
