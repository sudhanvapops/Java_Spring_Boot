package com.hql.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sf;
    
    static {
        try {
            Configuration cfg = new Configuration();
            cfg.configure();
            cfg.addAnnotatedClass(com.hql.model.Programmer.class);
            cfg.addAnnotatedClass(com.hql.model.Laptop.class);

            sf = cfg.buildSessionFactory();

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sf;
    }

}
