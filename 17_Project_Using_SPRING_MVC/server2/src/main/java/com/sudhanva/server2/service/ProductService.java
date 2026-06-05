package com.sudhanva.server2.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public Product getProductById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Product addProduct(Product product, MultipartFile image) throws IOException {

        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setImageData(image.getBytes());

        return repo.save(product);
    } 

}
