package com.orm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
// import org.hibernate.cfg.Configuration;

import com.orm.model.Student;
import com.orm.config.HibernateUtil;

public class Main {

    // just rewriting again

    public static void main(String[] args) {

        // ! Configuration
        // Configuration cfg = new Configuration();
        // // By doing this you say add this calls as Annotated and hibernate finds it 
        // cfg.addAnnotatedClass(com.orm.model.Student.class);
        // cfg.configure();
        // // Use once
        SessionFactory sf = HibernateUtil.getSessionFactory();
        
        
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