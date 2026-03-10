package com.second.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Laptop {

    @Id
    private int lid;
    private String brand;
    private String model;
    private int ram;

    @ManyToOne
    private Programmer programmer;
    // @ManyToMany(mappedBy = "laptops")
    // private List<Programmer> programmers;
    

    // public List<Programmer> getProgrammers() {
    //     return programmers;
    // }
    // public void setProgrammers(List<Programmer> programmers) {
    //     this.programmers = programmers;
    // }

    public Programmer getProgrammer() {
        return programmer;
    }
    public void setProgrammer(Programmer programmer) {
        this.programmer = programmer;
    }
    public int getLid() {
        return lid;
    }
    public void setLid(int lid) {
        this.lid = lid;
    }
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
