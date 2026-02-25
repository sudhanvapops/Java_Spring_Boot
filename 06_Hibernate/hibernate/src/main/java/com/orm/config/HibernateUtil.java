package com.orm.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration cfg = new Configuration();
            cfg.configure();
            cfg.addAnnotatedClass(com.orm.model.Student.class);
            cfg.addAnnotatedClass(com.orm.model.Alien.class);
            cfg.addAnnotatedClass(com.orm.model.Programmer.class);
            cfg.addAnnotatedClass(com.orm.model.Laptop.class);

            sessionFactory = cfg.buildSessionFactory();

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}