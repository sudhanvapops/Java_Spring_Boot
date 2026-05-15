package com.sudhanva.ten.Repository;

import org.springframework.stereotype.Repository;

import com.sudhanva.ten.Model.Laptop;


@Repository
public class LaptopRepository {
    
    public void save(Laptop laptop){
        System.out.println("Laptop Saved...");
    }

}
