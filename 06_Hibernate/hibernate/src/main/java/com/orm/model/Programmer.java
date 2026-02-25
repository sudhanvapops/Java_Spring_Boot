package com.orm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Programmer {
    @Id
    private int pid;
    private String name;
    private String tech;
    private Laptop laptop;
    
    @Override
    public String toString() {
        return "Programmer [pid=" + pid + ", name=" + name + ", tech=" + tech + ", laptop=" + laptop + "]";
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
    public Laptop getLaptop() {
        return laptop;
    }
    public void setLaptop(Laptop laptop) {
        this.laptop = laptop;
    }

    
}
