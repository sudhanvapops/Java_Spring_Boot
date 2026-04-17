package com.sudhanva.library.dao;

import java.util.List;
import java.util.Optional;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.sudhanva.library.entity.Book;


// Unnecessory for single impleemntation
interface BookDAOType {

    public Optional<Book> findByIsbn(String isbn);

    public List<Book> findByName(String name,int limit, int offset);

    public void save(Book book);

}

public class BookDAO implements BookDAOType{

    private SessionFactory sf;

    public BookDAO(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {

        // Since it is filed that exits we use getSingleResult()
        // But we are using OPtioanl and have to handle null case so use uniqueResultOPtional
        Session session = sf.getCurrentSession();

        Query<Book> q = session.createQuery("from Book b where b.isbn = :isbn",Book.class);

        q.setParameter("isbn", isbn);

        return q.uniqueResultOptional();
        
    }

    @Override
    public List<Book> findByName(String name,int limit, int offset) {
        Session session = sf.getCurrentSession();
        Query<Book> q = session.createQuery("from Book b where b.bookName  = :name",Book.class);

        q.setParameter("name", name);
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    @Override
    public void save(Book book) {
        Session session = sf.getCurrentSession();
        session.persist(book);
    }

}
