package com.sudhanva.ten.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.ten.Model.Laptop;
import com.sudhanva.ten.Repository.LaptopRepository;


@Service
public class LaptopService {

    private LaptopRepository laptopRepository;

    public LaptopRepository getLaptopRepository() {
        return laptopRepository;
    }

    @Autowired
    public void setLaptopRepository(LaptopRepository laptopRepository) {
        this.laptopRepository = laptopRepository;
    }

    public void addLaptop(Laptop laptop){
        laptopRepository.save(laptop);
    }

}
