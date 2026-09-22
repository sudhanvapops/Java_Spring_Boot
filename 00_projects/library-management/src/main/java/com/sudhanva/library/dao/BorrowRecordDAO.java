package com.sudhanva.library.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.sudhanva.library.entity.Book;
import com.sudhanva.library.entity.BorrowRecord;
import com.sudhanva.library.entity.Borrower;

public class BorrowRecordDAO {

    SessionFactory sf;

    public BorrowRecordDAO(SessionFactory sf){
        this.sf = sf;
    }

    public Optional<BorrowRecord> findActiveBorrowRecord(Borrower borrower,Book book){

        Session session = sf.getCurrentSession();

        Query<BorrowRecord> q =  session.createQuery("FROM BorrowRecord b WHERE b.borrower = :borrower and b.book = :book and b.returnDate is null ",BorrowRecord.class);

        q.setParameter("borrower",borrower);
        q.setParameter("book",book);

        return q.uniqueResultOptional();

    }

    public List<BorrowRecord> findAll(int limit, int offset) {
        Session session = sf.getCurrentSession();
        Query<BorrowRecord> q = session.createQuery("from BorrowRecord b order by b.id", BorrowRecord.class);

        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    public List<BorrowRecord> findByBorrower(Borrower borrower) {
        Session session = sf.getCurrentSession();
        Query<BorrowRecord> q = session.createQuery(
                "from BorrowRecord b where b.borrower = :borrower order by b.borrowDate desc", BorrowRecord.class);

        q.setParameter("borrower", borrower);

        return q.getResultList();
    }

}
