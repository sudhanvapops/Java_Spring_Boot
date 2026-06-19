package com.sudhanva.server2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.server2.Model.dto.OrderRequest;
import com.sudhanva.server2.Model.dto.OrderResponse;
import com.sudhanva.server2.service.OrderService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {
    
    @Autowired
    OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> response = orderService.getAllOrderResponses();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
    
    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderResponse);
    }

}
