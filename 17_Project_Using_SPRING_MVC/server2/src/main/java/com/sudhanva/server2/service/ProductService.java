package com.sudhanva.server2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.server2.Model.Product;
import com.sudhanva.server2.repo.ProductRepo;


@Service
public class ProductService {
    
    @Autowired
    private ProductRepo repo;

    public List<Product> getAllProducts(){
        return repo.findAll();
    }

    public void loadData(List<Product> product){
        repo.saveAll(product);
    }

}
