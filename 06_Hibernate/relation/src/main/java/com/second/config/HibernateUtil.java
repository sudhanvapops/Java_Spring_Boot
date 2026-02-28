package com.second.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration cfg = new Configuration();
            cfg.configure();
            cfg.addAnnotatedClass(com.second.model.Programmer.class);
            cfg.addAnnotatedClass(com.second.model.Laptop.class);

            sessionFactory = cfg.buildSessionFactory();

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}