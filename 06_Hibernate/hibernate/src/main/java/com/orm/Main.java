package com.orm;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
// import org.hibernate.cfg.Configuration;

import com.orm.model.Alien;
import com.orm.model.Student;
import com.orm.config.HibernateUtil;

public class Main {

    public static <T> void insertData(SessionFactory sf,T t ) {
        Session s = sf.openSession();
        Transaction trx = s.beginTransaction();
        s.persist(t);
        trx.commit();
        s.close();
    }

    public static Student fetchData(SessionFactory sf, Integer id) {
        // Since not fetching no need of transaction
        Session s = sf.openSession();
        Student st = s.find(Student.class, id);
        s.close();
        return st;

        // Output: if id: 5
        // Student: { rollNo: 5, name: Akash KR 2, sAge: 21 }
    }

    public static Student updateData(SessionFactory sf) {
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        // fetch it
        Student a = s.find(Student.class, 5);

        // Modify object
        a.setsAge(22);

        // No method needed

        tr.commit();
        s.close();
        return a;
    }

    public static Student updateDataMerge(SessionFactory sf) {
        Student detached = fetchData(sf, 5);

        Session s = sf.openSession();
        Transaction trx = s.beginTransaction();

        // returns a new Persistent object.
        Student mannaged = (Student) s.merge(detached);
        mannaged.setsAge(23);

        trx.commit();
        s.close();

        return detached;
    }

    public static Student deleteData(SessionFactory sf) {
        Student dt = fetchData(sf, 5);

        Session s = sf.openSession();
        Transaction trx = s.beginTransaction();

        Student mg = (Student) s.merge(dt);
        s.remove(mg);

        trx.commit();
        s.close();

        return mg;
    }

    public static Student prepreStudent() {
        // ! Data
        Student s1 = new Student();
        s1.setName("Akash KR 2");
        s1.setRollNo(6);
        s1.setsAge(21);
        return s1;
    }

    public static Alien prepreAlien(int id, String aname, String tech,int age) {
        // ! Data
        Alien a = new Alien();
        a.setAid(id);
        a.setAname(aname);
        a.setTech(tech);
        a.setAge(age);
        return a;
    }

    public static void main(String[] args) {

        try (// ! Configuration
             // Configuration cfg = new Configuration();
             // // By doing this you say add this calls as Annotated and hibernate finds it
             // cfg.addAnnotatedClass(com.orm.model.Student.class);
             // cfg.configure();
             // // Use once
            SessionFactory sf = HibernateUtil.getSessionFactory()) {

                // Fetch + Modify + Commit
                // MUST happen in SAME SESSION

                // ! inserting to db
                // insertData(s1, sf);

                // ! Fetching the Data
                // Student s = fetchData(sf, 5);
                // System.out.print("Fetch Result: ");
                // System.out.println(s + "\n");

                // ! Update Data
                // s = updateData(sf);
                // System.out.print("Update Result: ");
                // System.out.println(s + "\n");

                // ! deleteData
                // s = deleteData(sf);
                // System.out.print("Delete Result: ");
                // System.out.println(s + "\n");

                // ! 141 Alien

                // Data
                Alien a = prepreAlien(1, "Sudhanva", "Java",22);

                insertData(sf,a);

                // ! Close Connection
                // sf.close();
                // automatically handled by try catch
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}