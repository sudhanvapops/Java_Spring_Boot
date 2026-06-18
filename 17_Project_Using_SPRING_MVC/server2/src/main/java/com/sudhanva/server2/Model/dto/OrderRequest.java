package com.sudhanva.server2.Model.dto;

import java.util.List;

public record OrderRequest(
    String customerName,
    String email,
    List<OrderItemRequest> items
) {
    
}
