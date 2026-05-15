package com.sudhanva.ten.Service;

import org.springframework.stereotype.Service;

import com.sudhanva.ten.Model.Laptop;


@Service
public class LaptopService {

    public void addLaptop(Laptop laptop){
        System.out.println("Added laptop....");
    }

}
