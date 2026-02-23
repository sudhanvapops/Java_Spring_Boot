package com.orm;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
// import org.hibernate.cfg.Configuration;

import com.orm.model.Student;
import com.orm.config.HibernateUtil;

public class Main {

    public static void insertData(Student s1,SessionFactory sf) {
        Session s = sf.openSession();

        Transaction tr = s.beginTransaction();
        s.persist(s1);
        tr.commit();
        s.close();
    }

    public static Student fetchData(SessionFactory sf, Integer id){
        // Since not fetching no need of transaction
        Session s = sf.openSession();
        Student st = s.find(Student.class,id);
        return st;

        // Output: if id: 5
        // Student: { rollNo: 5, name: Akash KR 2, sAge: 21 }
    }

    public static void updateData(SessionFactory sf){
        
    }

    public static void main(String[] args) {

        try (// ! Configuration
                // Configuration cfg = new Configuration();
                // // By doing this you say add this calls as Annotated and hibernate finds it
                // cfg.addAnnotatedClass(com.orm.model.Student.class);
                // cfg.configure();
                // // Use once
        SessionFactory sf = HibernateUtil.getSessionFactory()) {
            // ! Data
            Student s1 = new Student();
            s1.setName("Akash KR 2");
            s1.setRollNo(6);
            s1.setsAge(21);

            // ! inserting to db
            // insertData(s1, sf);

            // ! Fetching the Data
            Student s = fetchData(sf,20);
            System.out.print("Result: ");
            System.out.println(s);

            // ! Update Data

            
            // ! Close Connection
            // sf.close();
            // automatically handled by try catch
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}