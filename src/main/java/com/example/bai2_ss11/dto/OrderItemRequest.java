package com.example.bai2_ss11.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderItemRequest(
        @JsonProperty("productId") @JsonAlias({"product_id", "id"}) String productId,
        @JsonProperty("productName") @JsonAlias({"product_name", "name"}) String productName,
        @JsonProperty("quantity") @JsonAlias({"qty", "amount"}) Integer quantity,
        @JsonProperty("unitPrice") @JsonAlias({"unit_price", "price"}) BigDecimal unitPrice
) {
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}

