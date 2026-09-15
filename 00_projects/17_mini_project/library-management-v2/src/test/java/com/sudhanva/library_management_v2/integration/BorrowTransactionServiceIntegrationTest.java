package com.sudhanva.library_management_v2.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;
import com.sudhanva.library_management_v2.Service.LibrarySettingsService;
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;

@SpringBootTest
@Testcontainers
public class BorrowTransactionServiceIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = 
            new PostgreSQLContainer<>("postgres:16-alpine");

    
    // DEpendecy Repos and Services
    @Autowired 
    private BorrowTransactionService borrowTransactionService;
    @Autowired 
    private LibrarySettingsService librarySettingsService;
    @Autowired 
    private MemberRepo memberRepo;
    @Autowired 
    private BookRepo bookRepo;


    private Member member;
    private Book book;


    // Test Data
    @BeforeEach
    void setup(){

        member = memberRepo.save(
            Member.builder()
                .name("Test Member 1")
                .email("test-" + System.nanoTime() + "@example.com")
                .age(25)
                .isActive(true)
                .build()
        );

        book = bookRepo.save(
            Book.builder()
                .name("Integration Testing 101")
                .author("Some Author")
                .isbn("TEST-" + System.nanoTime()) // NOT NULL column, value itself isn't the point here
                .availableCopy(2)
                .totalCopies(2)
                .isActive(true)
                .build()
        );
    }



    @Test
    void memberBorrowsBook_persistsTransactionAndDecrementsAvailableCopy(){

        BorrowTransactionRequest request = 
                BorrowTransactionRequest
                    .builder()
                    .memberId(member.getId())
                    .books(
                        List.of(
                            BorrowTransactionItemRequest
                            .builder().bookId(book.getId()).build()
                        )
                    )
                    .build();

        ApiResponse<BorrowTransactionResponse> response = 
                borrowTransactionService.borrowBook(request);
        
        

        // Three cases

        // is it success
        assertTrue(response.success());
        // How much i borrowed
        assertEquals(1, response.data().books().size());
        // is boorwed book id is correct
        assertEquals(book.getId(), response.data().books().get(0).borrowedBookId());

        int maxBorrowDays = librarySettingsService.getIntSetting(SettingKey.MAX_BORROW_DAYS);
        LocalDateTime dueDate = response.data().books().get(0).dueDate();


        // check if the due date is correct

        // Is Due date after the Borrow date - 1
        // simply is the book not expired check it before the due date 
        assertTrue(dueDate.isAfter(LocalDateTime.now().plusDays(maxBorrowDays - 1)));

        // Is Due date is before the Expiry date 
        // simply is teh book expired chekc the due date after the expriy date
        assertTrue(dueDate.isBefore(LocalDateTime.now().plusDays(maxBorrowDays + 1)));

        // check if The availbale copy reduced by one
        // re reaed from the db not the object stored inside the memory
        Book reloadedBook = bookRepo.findById(book.getId()).orElseThrow();
        assertEquals(1, reloadedBook.getAvailableCopy());


    }


}
