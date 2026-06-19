package com.sudhanva.server2.Model.dto;

public record OrderItemRequest(
    Long productId,
    int quantity
) {}
