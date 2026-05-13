package com.sudhanva;

public class Laptop {
    
    String cpu;

    public Laptop(String cpu){
        this.cpu = cpu;
    }

    public void compile(){
        System.out.println("Compiling Code With "+cpu);
    }
}
