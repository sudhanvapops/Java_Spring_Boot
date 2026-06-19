package com.sudhanva.server2.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.server2.Model.Order;
import com.sudhanva.server2.Model.OrderItem;
import com.sudhanva.server2.Model.Product;
import com.sudhanva.server2.Model.dto.OrderItemRequest;
import com.sudhanva.server2.Model.dto.OrderItemResponse;
import com.sudhanva.server2.Model.dto.OrderRequest;
import com.sudhanva.server2.Model.dto.OrderResponse;
import com.sudhanva.server2.repo.OrderRepo;
import com.sudhanva.server2.repo.ProductRepo;

@Service
public class OrderService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    public OrderResponse placeOrder(OrderRequest request) {

        // Make Order Id
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Build the order
        Order order = Order.builder()
                .orderId(orderId)
                .customerName(request.customerName())
                .email(request.email())
                .status("PLACED")
                .orderDate(LocalDate.now())
                .build();


        // Put all the built order items here
        List<OrderItem> orderItems = new ArrayList<>();

        // Take all the order itmes from request
        for (OrderItemRequest itmReq : request.items()) {

            // find the order by Id
            Product product = productRepo
                    .findById(itmReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));


            // Set the stock quantity in backend 
            product.setStockQuantity(product.getStockQuantity() - itmReq.quantity());


            // Build each order item 
            OrderItem orderItem = OrderItem
                    .builder()
                    .product(product)
                    .quantity(itmReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itmReq.quantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        // Adding orderItems 
        order.setOrderItems(orderItems);

        // Add to DataBase
        Order savedOrder = orderRepo.save(order);


        // Making Response

        // Item reponse means here the Item including in reponse not whole reponse
        List<OrderItemResponse> itmRes = new ArrayList<>();

        // For each order items in the current order
        for (OrderItem orderItem : order.getOrderItems()) {

            // Only choose these filds from orderItem
            OrderItemResponse orderItemResponse = OrderItemResponse
                    .builder()
                    .productName(orderItem.getProduct().getName())
                    .quantity(orderItem.getQuantity())
                    .totalPrice(orderItem.getTotalPrice())
                    .build();

            itmRes.add(orderItemResponse);
        }


        // Build Reposnse Object
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(savedOrder.getOrderId())
                .customerName(savedOrder.getCustomerName())
                .email(savedOrder.getEmail())
                .status(savedOrder.getStatus())
                .orderDate(savedOrder.getOrderDate())
                .items(itmRes)
                .build();

        // Return Repsonse
        return orderResponse;
    }


    public List<OrderResponse> getAllOrderResponses() {

        // Query the db for all the order done by that id
        List<Order> orders = orderRepo.findAll();

        // Since there can be many orders
        // so list
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItemResponse> itmRes = new ArrayList<>();

            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemResponse orderItemResponse = OrderItemResponse
                        .builder()
                        .productName(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .totalPrice(orderItem.getTotalPrice())
                        .build();

                itmRes.add(orderItemResponse);
            }

            OrderResponse orderResponse = OrderResponse
                    .builder()
                    .orderId(order.getOrderId())
                    .customerName(order.getCustomerName())
                    .email(order.getEmail())
                    .status(order.getStatus())
                    .orderDate(order.getOrderDate())
                    .items(itmRes)
                    .build();
            
            orderResponses.add(orderResponse);
        }

        return orderResponses;

    }

}
