package com.sudhanva.server2.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sudhanva.server2.Model.Order;
import com.sudhanva.server2.Model.dto.OrderRequest;
import com.sudhanva.server2.Model.dto.OrderResponse;

@Service
public class OrderService {

    public OrderResponse placeOrder(OrderRequest request) {

        String orderId = "ORD"+UUID.randomUUID().toString().substring(0,8).toUpperCase();

        Order order = Order.builder()
                        .orderId(orderId)
                        .customerName(request.customerName())
                        .email(request.email())
                        .status("PLACED")
                        .orderDate(LocalDate.now())
                        .build();
    }

    public List<OrderResponse> getAllOrderResponses() {
        return null;
    }
    
}
