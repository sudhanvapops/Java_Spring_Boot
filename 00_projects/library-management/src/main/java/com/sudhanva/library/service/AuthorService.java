package com.sudhanva.library.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sudhanva.library.dao.AuthorDAO;
import com.sudhanva.library.dto.AuthorDTO;
import com.sudhanva.library.dto.CreateAuthorRequest;
import com.sudhanva.library.entity.Author;

public class AuthorService {

    private final SessionFactory sf;
    private final AuthorDAO authorDAO;

    public AuthorService(SessionFactory sf) {
        this.sf = sf;
        this.authorDAO = new AuthorDAO(sf);
    }

    // Package-visible constructor for unit tests to inject mock DAOs
    AuthorService(SessionFactory sf, AuthorDAO authorDAO) {
        this.sf = sf;
        this.authorDAO = authorDAO;
    }

    public List<AuthorDTO> listAuthors(int limit, int offset) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            List<AuthorDTO> authors = authorDAO.findAll(limit, offset).stream()
                    .map(AuthorDTO::from)
                    .collect(Collectors.toList());
            trx.commit();
            return authors;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public Optional<AuthorDTO> getById(Long id) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Optional<AuthorDTO> author = authorDAO.findById(id).map(AuthorDTO::from);
            trx.commit();
            return author;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public AuthorDTO createAuthor(CreateAuthorRequest request) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Author author = new Author();
            author.setAuthorName(request.authorName);
            author.setEmail(request.email);
            author.setNationality(request.nationality);

            authorDAO.save(author);
            AuthorDTO dto = AuthorDTO.from(author);
            trx.commit();
            return dto;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public void deleteAuthor(Long id) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Author author = authorDAO.findById(id).orElseThrow(() -> new RuntimeException("Author not found"));
            authorDAO.delete(author);
            trx.commit();
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }
}
