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
            sf = cfg.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sf;
    }
}