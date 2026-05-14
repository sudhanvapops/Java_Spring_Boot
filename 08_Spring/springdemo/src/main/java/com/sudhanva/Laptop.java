package com.sudhanva;

public class Laptop implements Computer {
    
    String cpu;

    public Laptop(String cpu){
        this.cpu = cpu;
    }

    @Override
    public void compile(){
        System.out.println("Compiling Code With Laptop: "+cpu);
    }
}
