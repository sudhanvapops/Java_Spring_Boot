package com.sudhanva.library.dao;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.sudhanva.library.entity.Borrower;

import java.util.List;
import java.util.Optional;

public class BorrowerDAO {

    private SessionFactory sessionFactory;

    public BorrowerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<Borrower> findByCardNumber(String cardNumber) {

        Session s = sessionFactory.getCurrentSession();

        Query<Borrower> q = s.createQuery("FROM Borrower b WHERE b.cardNumber = :cardNumber", Borrower.class);

        q.setParameter("cardNumber", cardNumber);

        // avoids null
        return q.uniqueResultOptional();
    }

    // public List<Borrower> findByName(String name){
    // making it better
    // pagination and limit is handeled by service
    public List<Borrower> findByName(String name, int limit, int offset) {

        Session s = sessionFactory.getCurrentSession();

        Query<Borrower> q = s.createQuery("FROM Borrower b WHERE b.name = :name", Borrower.class);

        q.setParameter("name", name);
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    
    public void save(Borrower borrower) {
        Session s = sessionFactory.getCurrentSession();
        s.persist(borrower);
    }

    // Transaction is in service layer

}
