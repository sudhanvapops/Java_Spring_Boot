package com.sudhanva.server2.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.server2.Model.Product;
import com.sudhanva.server2.service.ProductService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService pService;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return pService.getAllProducts();
    }

    @GetMapping("/load")
    public void loadData() {
        List<Product> products = Arrays.asList(
                new Product(
                        null,
                        "iPhone 15",
                        "Apple flagship smartphone",
                        "Apple",
                        "Electronics",
                        new BigDecimal("79999.00"),
                        Date.valueOf("2024-09-20"),
                        true,
                        50),
                new Product(
                        null,
                        "Galaxy S24",
                        "Samsung premium smartphone",
                        "Samsung",
                        "Electronics",
                        new BigDecimal("74999.00"),
                        Date.valueOf("2024-01-18"),
                        true,
                        35),
                new Product(
                        null,
                        "Air Jordan 1",
                        "Classic basketball shoes",
                        "Nike",
                        "Footwear",
                        new BigDecimal("12999.00"),
                        Date.valueOf("2023-11-10"),
                        true,
                        120)
        );
        pService.loadData(products);
    }

    

}
