package com.sudhanva.server2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.server2.Model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long>{
    
}
