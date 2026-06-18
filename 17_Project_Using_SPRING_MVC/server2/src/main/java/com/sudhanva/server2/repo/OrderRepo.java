package com.sudhanva.server2.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sudhanva.server2.Model.Order;


public interface OrderRepo extends JpaRepository<Order,Long>{
    Optional<Order> findByOrderId(String orderId);
}
