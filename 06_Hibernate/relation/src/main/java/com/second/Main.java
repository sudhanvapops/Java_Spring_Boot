package com.second;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.second.config.HibernateUtil;
import com.second.model.Laptop;
import com.second.model.Programmer;

public class Main {

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
        try (
                SessionFactory sf = HibernateUtil.getSessionFactory();
            ) {

                Session session = sf.openSession();

                Transaction trx = session.beginTransaction();
                
                Laptop l1 =  prepreLaptop(16, "Asus", "ROG", 101);
                Laptop l2 =  prepreLaptop(32, "Asus", "TUF", 102);
                Laptop l3 =  prepreLaptop(64, "Asus", "VIVO", 103);


                Programmer p1 = prepreProgrammer(1, Arrays.asList(l1,l3), "Sudhanva", "Java");
                Programmer p2 = prepreProgrammer(2, Arrays.asList(l2), "Akasha", "Python");

                // Programmer p3 = prepreProgrammer(3, Arrays.asList(l1), "Anusha", "C++");

                // Added Which Laptop belongs to which programmer
                l1.setProgrammer(p1);
                l2.setProgrammer(p2);
                l3.setProgrammer(p1);
                


                session.persist(l1);
                session.persist(l2);
                session.persist(l3);

                session.persist(p1);
                session.persist(p2);
                // session.persist(p3);

                trx.commit();
                session.close();

                Session session2 = sf.openSession();
                Programmer p = session2.find(Programmer.class,1);
                System.out.println(p);
                session2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}