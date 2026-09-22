package com.sudhanva.library.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.sudhanva.library.entity.Author;

public class AuthorDAO {

    private final SessionFactory sf;

    public AuthorDAO(SessionFactory sf) {
        this.sf = sf;
    }

    public Optional<Author> findById(Long id) {
        Session session = sf.getCurrentSession();
        return Optional.ofNullable(session.get(Author.class, id));
    }

    public List<Author> findAll(int limit, int offset) {
        Session session = sf.getCurrentSession();
        Query<Author> q = session.createQuery("from Author a order by a.id", Author.class);

        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    public void save(Author author) {
        Session session = sf.getCurrentSession();
        session.persist(author);
    }

    public void delete(Author author) {
        Session session = sf.getCurrentSession();
        session.remove(author);
    }
}
