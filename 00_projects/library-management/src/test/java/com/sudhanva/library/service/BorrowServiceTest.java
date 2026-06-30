package com.sudhanva.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.sudhanva.library.dao.BookDAO;
import com.sudhanva.library.dao.BorrowRecordDAO;
import com.sudhanva.library.dao.BorrowerDAO;
import com.sudhanva.library.dto.BorrowRecordDTO;
import com.sudhanva.library.entity.Book;
import com.sudhanva.library.entity.BorrowRecord;
import com.sudhanva.library.entity.Borrower;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private BorrowerDAO borrowerDAO;
    @Mock
    private BookDAO bookDAO;
    @Mock
    private BorrowRecordDAO borrowRecordDAO;

    private BorrowService borrowService;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        borrowService = new BorrowService(sessionFactory, borrowerDAO, bookDAO, borrowRecordDAO);
    }

    @Test
    void borrowBook_happyPath_createsRecordAndDecrementsCopies() {
        Borrower borrower = sampleBorrower();
        Book book = sampleBook(2);
        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(borrower));
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));

        BorrowRecordDTO result = borrowService.borrowBook("CARD001", "ISBN001");

        assertThat(result.borrowerCardNumber).isEqualTo("CARD001");
        assertThat(result.bookIsbn).isEqualTo("ISBN001");
        assertThat(result.active).isTrue();
        assertThat(book.getAvailableCopies()).isEqualTo(1);
        verify(session).persist(org.mockito.ArgumentMatchers.any(BorrowRecord.class));
        verify(transaction).commit();
    }

    @Test
    void borrowBook_borrowerNotFound_throws() {
        when(borrowerDAO.findByCardNumber("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.borrowBook("MISSING", "ISBN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Borrower not found");

        verify(transaction).rollback();
    }

    @Test
    void borrowBook_bookNotFound_throws() {
        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(sampleBorrower()));
        when(bookDAO.findByIsbn("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.borrowBook("CARD001", "MISSING"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");
    }

    @Test
    void borrowBook_noCopiesAvailable_throws() {
        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(sampleBorrower()));
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(sampleBook(0)));

        assertThatThrownBy(() -> borrowService.borrowBook("CARD001", "ISBN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No Coppies Available");

        verify(transaction).rollback();
    }

    @Test
    void borrowBook_alreadyBorrowed_throws() {
        Book book = sampleBook(3);
        Borrower borrower = sampleBorrower();

        BorrowRecord activeRecord = new BorrowRecord();
        activeRecord.setBook(book);
        activeRecord.setBorrowDate(LocalDateTime.now());
        activeRecord.setReturnDate(null);
        borrower.addBorrowRecord(activeRecord);

        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(borrower));
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> borrowService.borrowBook("CARD001", "ISBN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Borrower already has this book");
    }

    @Test
    void returnBook_happyPath_marksReturnedAndIncrementsCopies() {
        Borrower borrower = sampleBorrower();
        Book book = sampleBook(1);
        BorrowRecord record = new BorrowRecord();
        record.setBorrower(borrower);
        record.setBook(book);
        record.setBorrowDate(LocalDateTime.now().minusDays(2));

        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(borrower));
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));
        when(borrowRecordDAO.findActiveBorrowRecord(borrower, book)).thenReturn(Optional.of(record));

        BorrowRecordDTO result = borrowService.returnBook("CARD001", "ISBN001");

        assertThat(result.active).isFalse();
        assertThat(result.returnDate).isNotNull();
        assertThat(book.getAvailableCopies()).isEqualTo(2);
        verify(transaction).commit();
    }

    @Test
    void returnBook_noActiveRecord_throws() {
        Borrower borrower = sampleBorrower();
        Book book = sampleBook(1);
        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(borrower));
        when(bookDAO.findByIsbn("ISBN001")).thenReturn(Optional.of(book));
        when(borrowRecordDAO.findActiveBorrowRecord(borrower, book)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.returnBook("CARD001", "ISBN001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No active borrow record found for this borrower and book");

        verify(transaction).rollback();
    }

    @Test
    void listRecords_returnsMappedDtos() {
        Borrower borrower = sampleBorrower();
        Book book = sampleBook(1);
        BorrowRecord record = new BorrowRecord();
        record.setBorrower(borrower);
        record.setBook(book);
        record.setBorrowDate(LocalDateTime.now());

        when(borrowRecordDAO.findAll(10, 0)).thenReturn(List.of(record));

        List<BorrowRecordDTO> result = borrowService.listRecords(10, 0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).active).isTrue();
    }

    @Test
    void listRecordsForBorrower_borrowerNotFound_throws() {
        when(borrowerDAO.findByCardNumber("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.listRecordsForBorrower("MISSING"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Borrower not found");
    }

    private Borrower sampleBorrower() {
        return new Borrower("sud@test.com", "CARD001", "Sudhanva", "9999999999", LocalDateTime.now());
    }

    private Book sampleBook(int availableCopies) {
        Book book = new Book();
        book.setBookName("1984");
        book.setIsbn("ISBN001");
        book.setTotalCopies(5);
        book.setAvailableCopies(availableCopies);
        return book;
    }
}
