package com.sudhanva.server2.Model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record OrderResponse(
    String orderId,
    String customerName,
    String email,
    String status,
    LocalDate orderDate,
    List<OrderItemResponse> items
) {
    
}
