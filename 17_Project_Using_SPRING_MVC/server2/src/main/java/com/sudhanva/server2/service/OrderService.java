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

        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderId(orderId)
                .customerName(request.customerName())
                .email(request.email())
                .status("PLACED")
                .orderDate(LocalDate.now())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itmReq : request.items()) {

            System.out.println("Product Id = " + itmReq.productId());
            Product product = productRepo
                    .findById(itmReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setStockQuantity(product.getStockQuantity() - itmReq.quantity());

            OrderItem orderItem = OrderItem
                    .builder()
                    .product(product)
                    .quantity(itmReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itmReq.quantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepo.save(order);

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

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(savedOrder.getOrderId())
                .customerName(savedOrder.getCustomerName())
                .email(savedOrder.getEmail())
                .status(savedOrder.getStatus())
                .orderDate(savedOrder.getOrderDate())
                .items(itmRes)
                .build();

        return orderResponse;
    }

    public List<OrderResponse> getAllOrderResponses() {

        List<Order> orders = orderRepo.findAll();
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
