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

import com.sudhanva.library.dao.BorrowerDAO;
import com.sudhanva.library.dto.BorrowerDTO;
import com.sudhanva.library.dto.CreateBorrowerRequest;
import com.sudhanva.library.dto.UpdateBorrowerRequest;
import com.sudhanva.library.entity.Borrower;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private BorrowerDAO borrowerDAO;

    private BorrowerService borrowerService;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        borrowerService = new BorrowerService(sessionFactory, borrowerDAO);
    }

    @Test
    void listBorrowers_returnsMappedDtos() {
        when(borrowerDAO.findAll(10, 0)).thenReturn(List.of(sampleBorrower()));

        List<BorrowerDTO> result = borrowerService.listBorrowers(10, 0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).cardNumber).isEqualTo("CARD001");
    }

    @Test
    void getByCardNumber_notFound_returnsEmpty() {
        when(borrowerDAO.findByCardNumber("MISSING")).thenReturn(Optional.empty());

        assertThat(borrowerService.getByCardNumber("MISSING")).isEmpty();
    }

    @Test
    void createBorrower_persistsAndReturnsDto() {
        CreateBorrowerRequest request = new CreateBorrowerRequest();
        request.name = "Sudhanva";
        request.email = "sud@test.com";
        request.cardNumber = "CARD001";
        request.phoneNo = "9999999999";

        BorrowerDTO result = borrowerService.createBorrower(request);

        assertThat(result.cardNumber).isEqualTo("CARD001");
        assertThat(result.membershipDate).isNotNull();
        verify(borrowerDAO).save(any(Borrower.class));
        verify(transaction).commit();
    }

    @Test
    void updateBorrower_appliesPartialChanges() {
        Borrower borrower = sampleBorrower();
        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(borrower));

        UpdateBorrowerRequest request = new UpdateBorrowerRequest();
        request.phoneNo = "1111111111";

        BorrowerDTO result = borrowerService.updateBorrower("CARD001", request);

        assertThat(result.phoneNo).isEqualTo("1111111111");
        assertThat(result.name).isEqualTo("Sudhanva");
    }

    @Test
    void deleteBorrower_notFound_rollsBackAndThrows() {
        when(borrowerDAO.findByCardNumber("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowerService.deleteBorrower("MISSING"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Borrower not found");

        verify(transaction).rollback();
    }

    @Test
    void deleteBorrower_found_deletesAndCommits() {
        Borrower borrower = sampleBorrower();
        when(borrowerDAO.findByCardNumber("CARD001")).thenReturn(Optional.of(borrower));

        borrowerService.deleteBorrower("CARD001");

        verify(borrowerDAO).delete(borrower);
        verify(transaction).commit();
    }

    private Borrower sampleBorrower() {
        return new Borrower("sud@test.com", "CARD001", "Sudhanva", "9999999999", java.time.LocalDateTime.now());
    }
}
