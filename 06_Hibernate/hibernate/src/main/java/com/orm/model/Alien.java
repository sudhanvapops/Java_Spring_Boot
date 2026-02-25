package com.orm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "alien_table")
public class Alien {
    @Id
    private int aid;
    @Column(name = "a_name")
    private String aname;
    private String tech;

    // This will make it only usabel in object 
    // and wont store in db
    @Transient
    private int age;

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getAid() {
        return aid;
    }
    public void setAid(int aid) {
        this.aid = aid;
    }
    public String getAname() {
        return aname;
    }
    public void setAname(String aname) {
        this.aname = aname;
    }
    public String getTech() {
        return tech;
    }
    public void setTech(String tech) {
        this.tech = tech;
    }
    @Override
    public String toString() {
        return "Alien: { aid: " + aid + ", aname: " + aname + ", tech: " + tech + ", age: " + age + " }";
    }
        
}
