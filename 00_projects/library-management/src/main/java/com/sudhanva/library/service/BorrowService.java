package com.sudhanva.library.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sudhanva.library.dao.BookDAO;
import com.sudhanva.library.dao.BorrowRecordDAO;
import com.sudhanva.library.dao.BorrowerDAO;
import com.sudhanva.library.dto.BorrowRecordDTO;
import com.sudhanva.library.entity.Book;
import com.sudhanva.library.entity.BorrowRecord;
import com.sudhanva.library.entity.Borrower;

public class BorrowService {

    SessionFactory sf;

    BorrowerDAO borrowerDAO;
    BookDAO bookDao;
    BorrowRecordDAO borrowRecordDAO;

    public BorrowService(SessionFactory sf) {
        this.sf = sf;
        this.borrowerDAO = new BorrowerDAO(sf);
        this.bookDao = new BookDAO(sf);
        this.borrowRecordDAO = new BorrowRecordDAO(sf);
    }

    // Package-visible constructor for unit tests to inject mock DAOs
    BorrowService(SessionFactory sf, BorrowerDAO borrowerDAO, BookDAO bookDao, BorrowRecordDAO borrowRecordDAO) {
        this.sf = sf;
        this.borrowerDAO = borrowerDAO;
        this.bookDao = bookDao;
        this.borrowRecordDAO = borrowRecordDAO;
    }


    
    public BorrowRecordDTO borrowBook(String cardNumber, String isbn) {

        // A Hibernate session is transaction-scoped You should not store it as a field
        Session session = sf.getCurrentSession();
        // Session session = sf.openSession();
        Transaction trx = session.beginTransaction();

        try {

            // Book and borrower both are Optional

            // 1. Fetch Borrower
            Borrower borrower = borrowerDAO.findByCardNumber(cardNumber).orElseThrow(() -> new RuntimeException("Borrower not found"));
            

            // 2. Fetch Book
            Book book =  bookDao.findByIsbn(isbn).orElseThrow(
                () -> new RuntimeException("Book not found")
            );


            //  3. Check availability using availableCopies
            if (book.getAvailableCopies() <= 0){
                throw new RuntimeException("No Coppies Available");
            }


            // 4. Check if book is already borrowed (active record exists)
            boolean alreadyBorrowed = borrower.getBorrowRecords().stream().anyMatch( record -> 
                record.getBook().equals(book) &&
                // null meneing he has book not returned
                record.isActive()
            );
            

            if (alreadyBorrowed){
                throw new RuntimeException("Borrower already has this book");
            }


            // 5. Decrement available copies
            book.setAvailableCopies(book.getAvailableCopies() - 1);


            // 6. Create BorrowRecord
            BorrowRecord record = new BorrowRecord();
            record.setBorrowDate(java.time.LocalDateTime.now());
            record.setReturnDate(null);
            // TODO: add DUE date
            // record.setDueDate();


            // 7. Maintain bidirectional relationships
            borrower.addBorrowRecord(record);
            book.addBorrowRecord(record);


            // 8. Persist only the new entity
            session.persist(record);


            trx.commit();
            return BorrowRecordDTO.from(record);


        } catch (Exception e) {
            trx.rollback();
            throw e;
        }

    }


    public BorrowRecordDTO returnBook(String cardNumber, String isbn){

        Session session = sf.getCurrentSession();

        Transaction trx = session.beginTransaction();

        try {

             // 1. Fetch borrower
            Borrower borrower = borrowerDAO.findByCardNumber(cardNumber).orElseThrow(
                () -> new RuntimeException("Borrower Not Found")
            );


            // 2. Fetch book
            Book book = bookDao.findByIsbn(isbn).orElseThrow(
                ()->new RuntimeException("Book Not Found")
            );


            // 3. Find active borrow record
            BorrowRecord br = borrowRecordDAO.findActiveBorrowRecord(borrower, book).orElseThrow(
                () -> new RuntimeException( "No active borrow record found for this borrower and book")
            );


            // 4. Mark as returned
            br.setReturnDate(java.time.LocalDateTime.now());

            // 5. Increase available copies
            book.setAvailableCopies(book.getAvailableCopies() + 1);


            // Updating The existing Record No need since Objects are automatically updated
            // borrower.addBorrowRecord(br);
            // book.addBorrowRecord(br);


            // Not needed cause br already exists and Hibernate will manage
            // session.persist(br);

            trx.commit();
            return BorrowRecordDTO.from(br);

        } catch (Exception e) {
            trx.rollback();
            throw e;
        }


    }

    public List<BorrowRecordDTO> listRecords(int limit, int offset) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            List<BorrowRecordDTO> records = borrowRecordDAO.findAll(limit, offset).stream()
                    .map(BorrowRecordDTO::from)
                    .collect(java.util.stream.Collectors.toList());
            trx.commit();
            return records;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public List<BorrowRecordDTO> listRecordsForBorrower(String cardNumber) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Borrower borrower = borrowerDAO.findByCardNumber(cardNumber)
                    .orElseThrow(() -> new RuntimeException("Borrower not found"));

            List<BorrowRecordDTO> records = borrowRecordDAO.findByBorrower(borrower).stream()
                    .map(BorrowRecordDTO::from)
                    .collect(java.util.stream.Collectors.toList());
            trx.commit();
            return records;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

}
