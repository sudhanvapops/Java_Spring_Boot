package com.sudhanva;

public class Desktop implements Computer{

    String cpu;

    public Desktop(String cpu){
        this.cpu = cpu;
    }

    @Override
    public void compile() {
        System.out.println("Compiling via Dekstop "+cpu);        
    }
    
}
