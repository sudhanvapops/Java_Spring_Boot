package com.sudhanva.library.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sudhanva.library.dao.BorrowerDAO;
import com.sudhanva.library.dto.BorrowerDTO;
import com.sudhanva.library.dto.CreateBorrowerRequest;
import com.sudhanva.library.dto.UpdateBorrowerRequest;
import com.sudhanva.library.entity.Borrower;

public class BorrowerService {

    private final SessionFactory sf;
    private final BorrowerDAO borrowerDAO;

    public BorrowerService(SessionFactory sf) {
        this.sf = sf;
        this.borrowerDAO = new BorrowerDAO(sf);
    }

    // Package-visible constructor for unit tests to inject mock DAOs
    BorrowerService(SessionFactory sf, BorrowerDAO borrowerDAO) {
        this.sf = sf;
        this.borrowerDAO = borrowerDAO;
    }

    public List<BorrowerDTO> listBorrowers(int limit, int offset) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            List<BorrowerDTO> borrowers = borrowerDAO.findAll(limit, offset).stream()
                    .map(BorrowerDTO::from)
                    .collect(Collectors.toList());
            trx.commit();
            return borrowers;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public Optional<BorrowerDTO> getByCardNumber(String cardNumber) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Optional<BorrowerDTO> borrower = borrowerDAO.findByCardNumber(cardNumber).map(BorrowerDTO::from);
            trx.commit();
            return borrower;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public BorrowerDTO createBorrower(CreateBorrowerRequest request) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Borrower borrower = new Borrower();
            borrower.setCardNumber(request.cardNumber);
            borrower.setName(request.name);
            borrower.setEmail(request.email);
            borrower.setPhoneNo(request.phoneNo);
            borrower.setMembershipDate(java.time.LocalDateTime.now());

            borrowerDAO.save(borrower);
            BorrowerDTO dto = BorrowerDTO.from(borrower);
            trx.commit();
            return dto;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public BorrowerDTO updateBorrower(String cardNumber, UpdateBorrowerRequest request) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Borrower borrower = borrowerDAO.findByCardNumber(cardNumber)
                    .orElseThrow(() -> new RuntimeException("Borrower not found"));

            if (request.name != null) {
                borrower.setName(request.name);
            }
            if (request.email != null) {
                borrower.setEmail(request.email);
            }
            if (request.phoneNo != null) {
                borrower.setPhoneNo(request.phoneNo);
            }

            BorrowerDTO dto = BorrowerDTO.from(borrower);
            trx.commit();
            return dto;
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }

    public void deleteBorrower(String cardNumber) {
        Session session = sf.getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Borrower borrower = borrowerDAO.findByCardNumber(cardNumber)
                    .orElseThrow(() -> new RuntimeException("Borrower not found"));
            borrowerDAO.delete(borrower);
            trx.commit();
        } catch (Exception e) {
            trx.rollback();
            throw e;
        }
    }
}
