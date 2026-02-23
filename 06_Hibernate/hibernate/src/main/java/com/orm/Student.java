package com.orm;

public class Student {

    private int rollNo;
    private String name;
    private int sAge;

    public int getRollNo() {
        return rollNo;
    }
    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getsAge() {
        return sAge;
    }
    public void setsAge(int sAge) {
        this.sAge = sAge;
    }
    @Override
    public String toString() {
        return "Student: { rollNo: " + rollNo + ", name: " + name + ", sAge: " + sAge + " }";
    }

    
    
}
