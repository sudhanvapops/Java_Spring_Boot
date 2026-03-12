package com.hql;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.STRING;

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
            Query<Laptop> queryL;
            List<Laptop> laptops;


            // Based on Primary Key
            // Laptop l1 = session.find(Laptop.class, 103);
            // System.out.println(l1);

            // Fetch all
            queryL = session.createQuery("from Laptop", Laptop.class);
            laptops = queryL.getResultList();
            // System.out.println("All Laptops:\n" + laptops);

            // Fetch by RAM
            queryL = session.createQuery("from Laptop where ram = :ram", Laptop.class);
            queryL.setParameter("ram", 8);
            laptops = queryL.getResultList();
            // System.out.println("\n8GB Laptops:\n" + laptops);

            // Fetch by Brand
            // u can aslo use like instead of = in string 
            queryL = session.createQuery("from Laptop where brand like 'ASUS' and ram = 16",Laptop.class);
            laptops = queryL.getResultList();
            // System.out.println(laptops);

            // Fetch by Brand By Givig Parameters
            // Avoid mixing positional and named parameters. Use only named parameters
            queryL = session.createQuery("from Laptop where brand like ?1 and ram = ?2 and model = :model",Laptop.class);
            queryL.setParameter(1, "ASUS");
            queryL.setParameter(2, "8");
            queryL.setParameter("model", "ROG");
            laptops = queryL.getResultList();
            // System.out.println(laptops);

            // Select Gives String
            Query<String> querySL = session.createQuery("select model from Laptop where brand like ?1",String.class);
            querySL.setParameter(1, "ASUS");
            List<String> laptopsString = querySL.getResultList();
            // System.out.println(laptopsString);


            // Multiple
            Query<Object[]> querySLO = session.createQuery("select brand,model from Laptop where brand like ?1",Object[].class);
            querySLO.setParameter(1, "ASUS");
            List<Object[]> laptopsObject = querySLO.getResultList();

            // for (Object[] objects : laptopsObject) {
            //     System.out.println((String)objects[0] +" "+ (String)objects[1]);
            // }

            Laptop l = session.getReference(Laptop.class,102);
            System.out.println(l);


            // trx.commit();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}