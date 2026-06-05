package com.sudhanva.server2.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sudhanva.server2.Model.Product;
import com.sudhanva.server2.service.ProductService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService pService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(pService.getAllProducts(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/load")
    public ResponseEntity<String> loadData() {
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
                        50, null, null, null),
                new Product(
                        null,
                        "Galaxy S24",
                        "Samsung premium smartphone",
                        "Samsung",
                        "Electronics",
                        new BigDecimal("74999.00"),
                        Date.valueOf("2024-01-18"),
                        true,
                        35, null, null, null),
                new Product(
                        null,
                        "Air Jordan 1",
                        "Classic basketball shoes",
                        "Nike",
                        "Footwear",
                        new BigDecimal("12999.00"),
                        Date.valueOf("2023-11-10"),
                        true,
                        120, null, null, null));
        pService.loadData(products);

        return new ResponseEntity<>("Data loaded successfully", HttpStatus.CREATED);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = pService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(
        @RequestPart Product product,
        @RequestPart MultipartFile imageFile
    ){

        Product savedProduct = null;
        try {
            savedProduct = pService.addProduct(product,imageFile);
            return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage());
        }

    }

}
