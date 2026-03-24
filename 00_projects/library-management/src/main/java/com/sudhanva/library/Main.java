package com.sudhanva.library;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sudhanva.library.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Transaction trx = null;
        try (
                Session session = sf.openSession();
            ) {
                trx = session.beginTransaction();
                System.out.println("Session opened successfully\n:");
    
                trx.commit();
        } catch (Exception e) {
            // Rollback if error Happened
            if (trx != null){
                trx.rollback();
            }
            e.printStackTrace();
        }
    }
}