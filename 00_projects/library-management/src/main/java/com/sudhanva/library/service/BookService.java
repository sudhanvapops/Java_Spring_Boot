package com.sudhanva.library.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sudhanva.library.dao.AuthorDAO;
import com.sudhanva.library.dao.BookDAO;
import com.sudhanva.library.dto.BookDTO;
import com.sudhanva.library.dto.CreateBookRequest;
import com.sudhanva.library.dto.UpdateBookRequest;
import com.sudhanva.library.entity.Author;
import com.sudhanva.library.entity.Book;

public class BookService {

    private final SessionFactory sf;
    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO;

    public BookService(SessionFactory sf) {
        this.sf = sf;
        this.bookDAO = new BookDAO(sf);
        this.authorDAO = new AuthorDAO(sf);
    }

    // Package-visible constructor for unit tests to inject mock DAOs
    BookService(SessionFactory sf, BookDAO bookDAO, AuthorDAO authorDAO) {
        this.sf = sf;
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
    }

    public List<BookDTO> listBooks(int limit, int offset) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            List<BookDTO> books = bookDAO.findAll(limit, offset).stream()
                    .map(BookDTO::from)
                    .collect(Collectors.toList());
            trx.commit();
            return books;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public Optional<BookDTO> getByIsbn(String isbn) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Optional<BookDTO> book = bookDAO.findByIsbn(isbn).map(BookDTO::from);
            trx.commit();
            return book;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public BookDTO createBook(CreateBookRequest request) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Book book = new Book();
            book.setBookName(request.bookName);
            book.setIsbn(request.isbn);
            book.setTotalCopies(request.totalCopies);
            book.setAvailableCopies(request.totalCopies);

            bookDAO.save(book);
            BookDTO dto = BookDTO.from(book);
            trx.commit();
            return dto;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public BookDTO updateBook(String isbn, UpdateBookRequest request) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Book book = bookDAO.findByIsbn(isbn).orElseThrow(() -> new RuntimeException("Book not found"));

            if (request.bookName != null) {
                book.setBookName(request.bookName);
            }
            if (request.totalCopies != null) {
                book.setTotalCopies(request.totalCopies);
            }
            if (request.availableCopies != null) {
                book.setAvailableCopies(request.availableCopies);
            }

            BookDTO dto = BookDTO.from(book);
            trx.commit();
            return dto;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public void deleteBook(String isbn) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Book book = bookDAO.findByIsbn(isbn).orElseThrow(() -> new RuntimeException("Book not found"));
            bookDAO.delete(book);
            trx.commit();
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public BookDTO addAuthorToBook(String isbn, Long authorId) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Book book = bookDAO.findByIsbn(isbn).orElseThrow(() -> new RuntimeException("Book not found"));
            Author author = authorDAO.findById(authorId).orElseThrow(() -> new RuntimeException("Author not found"));

            book.addAuthor(author);

            BookDTO dto = BookDTO.from(book);
            trx.commit();
            return dto;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }
}
