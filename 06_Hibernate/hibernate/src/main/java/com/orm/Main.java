package com.orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Main {

    // just rewriting again
    public static void test(){
        
        // ! 1. Configuration
        Configuration cfg = new Configuration();
        cfg.addAnnotatedClass(com.orm.Student.class);
        cfg.configure();

        // ! 2. Make Session Factory
        SessionFactory sf = cfg.buildSessionFactory();

        // Till Up only once 

        // !  Data to be added
        Student s1  = new Student();
        s1.setName("Aka");
        s1.setRollNo(4);
        s1.setsAge(21);

        // When ever you need to add just below code

        // ! Open the session
        Session session = sf.openSession();
        // ! Set a Transaction
        Transaction trx = session.beginTransaction();
        session.persist(s1);
        // Commit transaction
        trx.commit();

        // ! Close Connection
        session.close();
        sf.close();

    }
    
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
        s1.setName("Akash KR 2");
        s1.setRollNo(6);
        s1.setsAge(21);
        

        // ! inserting to db
        Session s = sf.openSession();

        Transaction tr = s.beginTransaction();
        s.persist(s1);
        tr.commit();        

        // ! Close Connection
        s.close();
        sf.close();
    }
}