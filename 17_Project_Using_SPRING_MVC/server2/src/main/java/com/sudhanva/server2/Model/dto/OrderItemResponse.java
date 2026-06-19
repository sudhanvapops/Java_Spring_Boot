package com.sudhanva.server2.Model.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record OrderItemResponse(
    String productName,
    int quantity,
    BigDecimal totalPrice
) {
    
}
