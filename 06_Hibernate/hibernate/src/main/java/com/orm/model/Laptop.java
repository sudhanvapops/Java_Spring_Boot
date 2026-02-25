package com.orm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// import jakarta.persistence.Embeddable;

// @Embeddable
@Entity
public class Laptop {
    
    @Id
    private int lid;
    public int getLid() {
        return lid;
    }
    public void setLid(int lid) {
        this.lid = lid;
    }
    private String brand;
    private String model;
    private int ram;
    
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getRam() {
        return ram;
    }
    public void setRam(int ram) {
        this.ram = ram;
    }

    @Override
    public String toString() {
        return "Laptop [lid=" + lid + ", brand=" + brand + ", model=" + model + ", ram=" + ram + "]";
    }

    
}
