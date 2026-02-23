package com.orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Main {


    public static void main(String[] args) {

        // ! Configuration
        Configuration cfg = new Configuration();
        // By doing this you say add this calls as Annotated and hibernate finds it 
        cfg.addAnnotatedClass(com.orm.Student.class);
        cfg.configure();
        // Use once
        SessionFactory sf = cfg.buildSessionFactory();
        
        
        // ! Data
        Student s1 = new Student();
        s1.setName("Akash KR");
        s1.setRollNo(2);
        s1.setsAge(21);
        

        // ! inserting to db
        Session s = sf.openSession();

        Transaction tr = s.beginTransaction();
        s.persist(s1);
        tr.commit();        

        System.out.println(s1);

        s.close();
        sf.close();
    }
}