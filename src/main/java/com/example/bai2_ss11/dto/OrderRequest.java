package com.example.bai2_ss11.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRequest(
        @JsonProperty("orderId") @JsonAlias({"order_id", "id", "orderID"}) String orderId,
        @JsonProperty("customerId") @JsonAlias({"customer_id", "userId", "user_id"}) String customerId,
        @JsonProperty("items") @JsonAlias({"orderItems", "order_items"}) List<OrderItemRequest> items,
        @JsonProperty("totalAmount") @JsonAlias({"total_amount", "amount", "total"}) BigDecimal totalAmount,
        @JsonProperty("shippingAddress") @JsonAlias({"shipping_address", "address"}) String shippingAddress
) {
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderItemRequest> getItems() { return items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
}

