package com.hql;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.hql.config.HibernateUtil;
import com.hql.model.Laptop;
import com.hql.model.Programmer;

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

    public static Programmer prepreProgrammer(int pid, String name, String tech) {
        // public static Programmer prepreProgrammer(int pid, List<Laptop> laptops,
        // String name, String tech) {
        // ! Data
        Programmer p = new Programmer();
        // p.setLaptop(laptop);
        // p.setLaptops(laptops);
        p.setName(name);
        p.setPid(pid);
        p.setTech(tech);
        return p;
    }

    public static void main(String[] args) {

        try (
                SessionFactory sf = HibernateUtil.getSessionFactory();) {

            Session session = sf.openSession();
            // Transaction trx = session.beginTransaction();

            // ! Fetching
            Query<Laptop> query;
            List<Laptop> laptops;

            // Based on Primary Key
            Laptop l1 = session.find(Laptop.class, 103);
            System.out.println(l1);

            // Fetch all
            query = session.createQuery("from Laptop", Laptop.class);
            laptops = query.getResultList();
            // System.out.println("All Laptops:\n" + laptops);

            // Fetch by RAM
            query = session.createQuery("from Laptop where ram = :ram", Laptop.class);
            query.setParameter("ram", 8);
            laptops = query.getResultList();
            System.out.println("\n8GB Laptops:\n" + laptops);

            // trx.commit();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}