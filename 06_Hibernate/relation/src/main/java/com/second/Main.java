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
                Session session = sf.openSession();) {

            Transaction trx = session.beginTransaction();
            
            Laptop l1 =  prepreLaptop(16, "Asus", "ROG", 101);

            Programmer p1 = prepreProgrammer(1, Arrays.asList(l1), "Sudhanva", "Java");

            session.persist(l1);
            session.persist(p1);

            trx.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}