package com.orm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
// import org.hibernate.cfg.Configuration;

import com.orm.model.Alien;
import com.orm.model.Laptop;
import com.orm.model.Programmer;
import com.orm.model.Student;
import com.orm.config.HibernateUtil;

public class Main {

    public static <T> void insertData(SessionFactory sf, T t, Session s, Transaction trx) {
        s.persist(t);
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

    public static Alien prepreAlien(int id, String aname, String tech, int age) {
        // ! Data
        Alien a = new Alien();
        a.setAid(id);
        a.setAname(aname);
        a.setTech(tech);
        a.setAge(age);
        return a;
    }

    public static Laptop prepreLaptop(int ram, String brand, String model, int lid) {
        // ! Data
        Laptop laptop = new Laptop();
        laptop.setBrand(brand);
        laptop.setModel(model);
        laptop.setRam(ram);
        laptop.setLid(lid);
        return laptop;
    }

    public static Programmer prepreProgrammer(int pid, List<Laptop> laptops, String name, String tech) {
        // ! Data
        Programmer p = new Programmer();
        // p.setLaptop(laptop);
        p.setLaptops(laptops);
        p.setName(name);
        p.setPid(pid);
        p.setTech(tech);
        return p;
    }

    public static void main(String[] args) {

        try (// ! Configuration
             // Configuration cfg = new Configuration();
             // // By doing this you say add this calls as Annotated and hibernate finds it
             // cfg.addAnnotatedClass(com.orm.model.Student.class);
             // cfg.configure();
             // // Use once
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
            ) {

            Transaction trx = s.beginTransaction();

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
            // Alien a = prepreAlien(1, "Sudhanva", "Java",22);

            // ! 142 Programmer Embedable
            // Laptop l = prepreLaptop(10, "Asus", "ROG",1);
            // List<Laptop> laptops = new ArrayList<>();
            Laptop l1 = prepreLaptop(16, "Asus", "ROG", 101);
            Laptop l2 = prepreLaptop(32, "Asus", "TUF", 102);
            Laptop l3 = prepreLaptop(8, "LENOVO", "LOQ", 103);
            Laptop l4 = prepreLaptop(4, "LENOVO", "LEGION", 104);

            List<Laptop> laptopP1 = new ArrayList<>();
            laptopP1.add(l1);
            laptopP1.add(l2);

            List<Laptop> laptopP2 = new ArrayList<>();
            laptopP2.add(l3);
            laptopP2.add(l4);


            // Insert Each Laptop Individually
            // insertData(sf, l1,s,trx);
            // insertData(sf, l2,s,trx);
            // insertData(sf, l3,s,trx);
            // insertData(sf, l4,s,trx);

            Programmer p1 = prepreProgrammer(1, laptopP1, "Sudhanva", "Java");
            Programmer p2 = prepreProgrammer(2, laptopP2, "Akasha", "JS");
            // Casecade On this gone
            // insertData(sf,Arrays.asList(l1,l2));

            insertData(sf, p1,s,trx);
            insertData(sf, p2,s,trx);

            trx.commit();
            s.close();

            // ! Close Connection
            // sf.close();
            // automatically handled by try catch
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}