package com.sudhanva.library;

import java.time.LocalDateTime;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sudhanva.library.entity.Author;
import com.sudhanva.library.entity.Book;
import com.sudhanva.library.entity.Borrower;
import com.sudhanva.library.service.BorrowService;
import com.sudhanva.library.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Transaction trx = null;

        Session session = sf.openSession();
        try {

            trx = session.beginTransaction();
            System.out.println("Session opened successfully\n:");

            // Test Data

            // // Author
            // Author orwell = new Author();
            // orwell.setAuthorName("George Orwell");
            // orwell.setEmail("orwell@gmail.com");
            // orwell.setNationality("British");

            // // Create Book
            // Book book1984 = new Book();
            // book1984.setBookName("1984");
            // book1984.setIsbn("ISBN001");
            // book1984.setTotalCopies(5);
            // book1984.setAvailableCopies(5);

            // // Connect Relationship
            // book1984.addAuthor(orwell);

            // // Step 4 — Create Borrower
            // Borrower borrower = new Borrower();
            // borrower.setName("Sudhanva");
            // borrower.setCardNumber("CARD001");
            // borrower.setEmail("sudhanva@gmail.com");
            // borrower.setPhoneNo("9999999999");
            // borrower.setMembershipDate(java.time.LocalDateTime.now());

            // // Step 5 — Persist
            // session.persist(orwell);
            // session.persist(book1984);
            // session.persist(borrower);

            // System.out.println("Seed data inserted");

            // 2. Testing Borrow Flow
            BorrowService service = new BorrowService(sf);

            // Test
            service.borrowBook("CARD001", "ISBN001");

            trx.commit();
        } catch (Exception e) {
            // Rollback if error Happened
            if (trx != null) {
                trx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}