package com.orm.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
// import jakarta.persistence.OneToOne;

@Entity
public class Programmer {
    @Id
    private int pid;
    private String name;
    private String tech;
    // @OneToOne
    // private Laptop laptop;
    // This will insert lid column to Programer

    @OneToMany(cascade = CascadeType.ALL)
    private List<Laptop> laptops;
    

    public List<Laptop> getLaptops() {
        return laptops;
    }

    public void setLaptops(List<Laptop> laptops) {
        this.laptops = laptops;
    }
   
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTech() {
        return tech;
    }
    public void setTech(String tech) {
        this.tech = tech;
    }

    @Override
    public String toString() {
        return "Programmer [pid=" + pid + ", name=" + name + ", tech=" + tech + ", laptops=" + laptops + "]";
    }

    // public Laptop getLaptop() {
    //     return laptop;
    // }
    // public void setLaptop(Laptop laptop) {
    //     this.laptop = laptop;
    // }

    

    
}
