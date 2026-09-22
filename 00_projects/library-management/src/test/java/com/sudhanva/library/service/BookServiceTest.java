package com.sudhanva.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sudhanva.library.dao.AuthorDAO;
import com.sudhanva.library.dao.BookDAO;
import com.sudhanva.library.dto.BookDTO;
import com.sudhanva.library.dto.CreateBookRequest;
import com.sudhanva.library.dto.UpdateBookRequest;
import com.sudhanva.library.entity.Author;
import com.sudhanva.library.entity.Book;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private BookDAO bookDAO;
    @Mock
    private AuthorDAO authorDAO;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        bookService = new BookService(sessionFactory, bookDAO, authorDAO);
    }

    @Test
    void listBooks_returnsMappedDtos() {
        Book book = sampleBook();
        when(bookDAO.findAll(10, 0)).thenReturn(List.of(book));

        List<BookDTO> result = bookService.listBooks(10, 0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isbn).isEqualTo("ISBN001");
        verify(transaction).commit();
    }

    @Test
    void getByIsbn_found_returnsDto() {
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(sampleBook()));

        Optional<BookDTO> result = bookService.getByIsbn("ISBN001");

        assertThat(result).isPresent();
        assertThat(result.get().bookName).isEqualTo("1984");
    }

    @Test
    void getByIsbn_notFound_returnsEmpty() {
        when(bookDAO.findByIsbn("MISSING")).thenReturn(Optional.empty());

        Optional<BookDTO> result = bookService.getByIsbn("MISSING");

        assertThat(result).isEmpty();
    }

    @Test
    void createBook_persistsAndReturnsDto() {
        CreateBookRequest request = new CreateBookRequest();
        request.bookName = "Dune";
        request.isbn = "ISBN-DUNE";
        request.totalCopies = 5;

        BookDTO result = bookService.createBook(request);

        assertThat(result.bookName).isEqualTo("Dune");
        assertThat(result.totalCopies).isEqualTo(5);
        assertThat(result.availableCopies).isEqualTo(5);
        verify(bookDAO).save(any(Book.class));
        verify(transaction).commit();
    }

    @Test
    void updateBook_appliesPartialChanges() {
        Book book = sampleBook();
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));

        UpdateBookRequest request = new UpdateBookRequest();
        request.totalCopies = 10;

        BookDTO result = bookService.updateBook("ISBN001", request);

        assertThat(result.totalCopies).isEqualTo(10);
        // bookName untouched because request.bookName was null
        assertThat(result.bookName).isEqualTo("1984");
    }

    @Test
    void updateBook_notFound_rollsBackAndThrows() {
        when(bookDAO.findByIsbn("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook("MISSING", new UpdateBookRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");

        verify(transaction).rollback();
    }

    @Test
    void deleteBook_found_deletesAndCommits() {
        Book book = sampleBook();
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));

        bookService.deleteBook("ISBN001");

        verify(bookDAO).delete(book);
        verify(transaction).commit();
    }

    @Test
    void deleteBook_notFound_throws() {
        when(bookDAO.findByIsbn("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook("MISSING"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");

        verify(transaction).rollback();
    }

    @Test
    void addAuthorToBook_linksAuthor() {
        Book book = sampleBook();
        Author author = new Author("George Orwell", "orwell@test.com", "British", new java.util.HashSet<>());
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));
        when(authorDAO.findById(1L)).thenReturn(Optional.of(author));

        BookDTO result = bookService.addAuthorToBook("ISBN001", 1L);

        assertThat(result.authorNames).contains("George Orwell");
    }

    @Test
    void addAuthorToBook_authorNotFound_throws() {
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(sampleBook()));
        when(authorDAO.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.addAuthorToBook("ISBN001", 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Author not found");
    }

    private Book sampleBook() {
        Book book = new Book();
        book.setBookName("1984");
        book.setIsbn("ISBN001");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        return book;
    }
}
