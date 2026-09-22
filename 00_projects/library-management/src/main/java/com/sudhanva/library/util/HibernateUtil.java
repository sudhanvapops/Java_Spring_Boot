package com.sudhanva.library.util;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sudhanva.library.entity.Author;
import com.sudhanva.library.entity.Book;
import com.sudhanva.library.entity.BorrowRecord;
import com.sudhanva.library.entity.Borrower;


public class HibernateUtil {

    private static final SessionFactory sf;

    static {
        try {
            Configuration cfg = new Configuration();
            cfg.addAnnotatedClass(Book.class);
            cfg.addAnnotatedClass(Author.class);
            cfg.addAnnotatedClass(Borrower.class);
            cfg.addAnnotatedClass(BorrowRecord.class);
            cfg.configure();

            // Allow overriding DB connection settings via env vars (falls back to hibernate.cfg.xml)
            overrideIfPresent(cfg, "hibernate.connection.url", "DB_URL");
            overrideIfPresent(cfg, "hibernate.connection.username", "DB_USERNAME");
            overrideIfPresent(cfg, "hibernate.connection.password", "DB_PASSWORD");

            sf = cfg.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void overrideIfPresent(Configuration cfg, String property, String envVar) {
        String value = System.getenv(envVar);
        if (value != null && !value.isBlank()) {
            cfg.setProperty(property, value);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sf;
    }
}