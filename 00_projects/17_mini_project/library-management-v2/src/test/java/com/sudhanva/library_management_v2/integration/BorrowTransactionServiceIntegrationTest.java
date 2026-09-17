package com.sudhanva.library_management_v2.integration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BookReturnRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BookReturnResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowReturnItemRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowReturnItemResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Service.BorrowRecordService;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;
import com.sudhanva.library_management_v2.Service.LibrarySettingsService;
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@SpringBootTest
@Testcontainers
// It doesn't automatically know that those parameters are supposed to be Spring beans.so down line
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BorrowTransactionServiceIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = 
            new PostgreSQLContainer<>("postgres:16-alpine");

    
    // DEpendecy Repos and Services
    private final BorrowTransactionService borrowTransactionService;
    private final BorrowRecordService borrowRecordService;
    private final LibrarySettingsService librarySettingsService;
    private final MemberRepo memberRepo;
    private final BookRepo bookRepo;
    private final JdbcTemplate jdbcTemplate;


    private Member member;
    private Book book;


    // Test Data
    @BeforeEach
    void setup(){


        member = memberRepo.save(
            Member.builder()
                .name("Test Member: "+ System.nanoTime())
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
                // .author("Some Author"+ System.nanoTime())
                .isbn("TEST-" + System.nanoTime()) // NOT NULL column, value itself isn't the point here
                .availableCopy(2)
                .totalCopies(2)
                .isActive(true)
                .build()
        );
    }


    // Clean Up The Table
    @AfterEach
    void cleanUp(){
        jdbcTemplate.execute("TRUNCATE TABLE borrow_record, borrow_transaction,users, book, member RESTART IDENTITY CASCADE");
    }

    @Test
    void memberBorrowsAndReturnsBook_persistsCorrectState(){

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


        // Return Book

        // prepare object
        BookReturnRequest returnRequest = BookReturnRequest
            .builder()
            .memberId(member.getId())
            .books(
                List.of(BorrowReturnItemRequest.builder().bookId(book.getId()).build())
            )
            .build();
        
        ApiResponse<BookReturnResponse> returnResponse = 
            borrowRecordService.returnBook(returnRequest);


        // book returned
        assertTrue(returnResponse.success());

        // check available copy incresed
        Book returnedBook = bookRepo.findById(book.getId()).orElseThrow();
        assertEquals(2, returnedBook.getAvailableCopy());

        // To check if the correct book is being returned
        assertEquals(book.getId(), returnResponse.data().books().get(0).bookId());

        // can also check fine
        assertEquals(BigDecimal.ZERO, returnResponse.data().totalFine());
    }


}
