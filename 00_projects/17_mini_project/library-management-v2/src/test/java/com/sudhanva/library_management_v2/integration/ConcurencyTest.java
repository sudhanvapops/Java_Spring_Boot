package com.sudhanva.library_management_v2.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Testcontainers
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ConcurencyTest {

    /*
     * B2 — SAME CONCURRENCY BUG IN JAVA / SPRING
     *
     * Goal:
     *
     * 1 PostgreSQL container
     * 10 concurrent requests
     * 10 different members
     * SAME book
     * availableCopy = 1
     *
     * We want all 10 requests to compete for the same database row.
     */

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    private final BorrowTransactionService borrowTransactionService;
    private final MemberRepo memberRepo;
    private final BookRepo bookRepo;
    private final JdbcTemplate jdbcTemplate;

    private List<Member> members;
    private Book book;

    @BeforeEach
    void setup() {

        members = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Member member = memberRepo.save(
                    Member.builder()
                            .name("Test Member " + i)
                            .email(
                                "test-"
                                    + System.nanoTime()
                                    + "-"
                                    + i
                                    + "@example.com")
                            .age(25)
                            .isActive(true)
                            .build());

            members.add(member);
        }


        book = bookRepo.save(
                Book.builder()
                        .name("Integration Testing 101")
                        .author("Some Author")
                        .isbn("TEST-" + System.nanoTime())
                        .availableCopy(1)
                        .totalCopies(1)
                        .isActive(true)
                        .build());
    }

    @AfterEach
    void cleanUp() {

        jdbcTemplate.execute(
                """
                        TRUNCATE TABLE
                            borrow_record,
                            borrow_transaction,
                            users,
                            book,
                            member
                        RESTART IDENTITY CASCADE
                        """);
    }



    @Test
    void sameTestMyVersion() throws Exception {

        // Make 10 threads
        // Wait till all the 10 threads submited
        // relase all at once
        // boorw the book all of them
        // wait till all finsihes

        int requestCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);


        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(10);

        List<Future<Boolean>> futureList = new ArrayList<>();

        int successfulRequests = 0;

        // have 10 concurrent requests, and you want all 10 to reach the gate and then say GO together.
        // all the thread encounter .await() and wait
        // when .countdown() hits all the threads go together


        for (int i=0; i<requestCount; i++){

            int memberIndex = i;

            Future<Boolean> future = executor.submit(()->{
                try {

                    startLatch.await();

                    System.out.println(Thread.currentThread().getName()+" Started request for member: "+memberIndex);

                    BorrowTransactionRequest request = 
                            BorrowTransactionRequest.builder()
                                .memberId(
                                        members
                                            .get(memberIndex)
                                            .getId())
                                .books(
                                        List.of(
                                            BorrowTransactionItemRequest
                                                .builder()
                                                .bookId(
                                                        book.getId())
                                                .build())
                                            )
                                .build();
                    
                    ApiResponse<BorrowTransactionResponse> response = borrowTransactionService
                            .borrowBook(request);

                    System.out.println(
                        Thread.currentThread().getName()
                            + " SUCCESS for member "
                            + memberIndex
                            + " -> "
                            + response
                    );
                    
                    return true;

                } finally {
                    finishLatch.countDown();
                }
            });

            futureList.add(future);
        }

        System.out.println("========== RELEASING 10 REQUESTS ==========");
        startLatch.countDown();
        // Wait for all 10 requests
        finishLatch.await();
        executor.shutdown();

        // Count Successfull Request
        for(Future<Boolean> future : futureList){
            if(future.get()){
                successfulRequests++;
            }
        }


        // Reloading the book from DB
        Book finalBook = bookRepo
            .findById(book.getId())
            .orElseThrow();

        Integer finalAvailableCopy = finalBook.getAvailableCopy();


        // Printing final states
        System.out.println();
        System.out.println("\n\n==========================================");
        System.out.println("CONCURRENCY TEST RESULT");
        System.out.println("==========================================");
        System.out.println(
                "Total requests     = " + requestCount);
        System.out.println(
                "Successful requests = " + successfulRequests);
        System.out.println(
                "Final available     = " + finalAvailableCopy);
        System.out.println("==========================================\n\n");

        
        assertNotNull(finalBook); // db book not null
        assertEquals(0, finalAvailableCopy,"\nAvailable copies should be 0 after the only copy is borrowed\n");
        assertEquals(1, successfulRequests,"\nOnly one of the 10 concurrent requests should succeed\n");

    }


    // 1 copy available
    // 10 successful borrowers
    // Concurency Bug
    // All will return 0 beacuse its doing -= 1 in its own object copy every request
    // so its not -ve

    // @Test
    void should_handle_10_concurrent_borrow_requests() throws Exception {

        int requestCount = 10;

        // One thread for each request
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);

        /*
         * START LATCH
         *
         * All 10 threads wait here.
         *
         * The test opens the gate only after all 10 tasks
         * have been submitted.
         */
        CountDownLatch startLatch = new CountDownLatch(1);

        /*
         * FINISH LATCH
         *
         * Starts at 10.
         *
         * Every request calls countDown() when it finishes.
         *
         * The test waits until this reaches 0.
         */
        CountDownLatch finishLatch = new CountDownLatch(requestCount);

        /*
         * Store the result of every concurrent request.
         */
        List<Future<Boolean>> results = new ArrayList<>();



        /*
         * Create 10 concurrent requests.
         */
        for (int i = 0; i < requestCount; i++) {

            int memberIndex = i;

            Future<Boolean> future = executor.submit(() -> {

                try {

                    /*
                     * WAIT
                     *
                     * Every thread reaches this point and waits.
                     */
                    startLatch.await();

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " STARTED request for member "
                                    + memberIndex);

                
                    BorrowTransactionRequest request = BorrowTransactionRequest.builder()
                            .memberId(
                                    members
                                            .get(memberIndex)
                                            .getId())
                            .books(
                                    List.of(
                                            BorrowTransactionItemRequest
                                                    .builder()
                                                    .bookId(
                                                            book.getId())
                                                    .build()))
                            .build();

                    /*
                     * ACTUAL CONCURRENT OPERATION
                     *
                     * This is the method whose transaction
                     * behavior we are testing.
                     */
                    ApiResponse<BorrowTransactionResponse> response = borrowTransactionService
                            .borrowBook(request);

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " SUCCESS for member "
                                    + memberIndex
                                    + " -> "
                                    + response);

                    return true;

                } finally {

                    /*
                     * Tell the test:
                     *
                     * "This request is finished."
                     */
                    finishLatch.countDown();
                }
            });

            results.add(future);
        }

        /*
         * RELEASE THE GATE
         *
         * All 10 threads are now allowed to continue.
         */
        System.out.println("========== RELEASING 10 REQUESTS ==========");

        startLatch.countDown();

        /*
         * WAIT FOR ALL 10 REQUESTS.
         */
        finishLatch.await();

        /*
         * No more tasks should be submitted.
         */
        executor.shutdown();

        /*
         * Count successful requests.
         */
        long successfulRequests = 0;

        for (Future<Boolean> future : results) {

            /*
             * future.get() will throw if the task itself
             * threw an exception.
             *
             * We deliberately DO NOT swallow the exception.
             */
            if (future.get()) {
                successfulRequests++;
            }
        }

        /*
         * Reload the book from the DATABASE.
         *
         * Don't use the original 'book' object because that may
         * represent an older persistence-context state.
         */
        Book finalBook = bookRepo
                .findById(book.getId())
                .orElseThrow();

        Integer finalAvailableCopies = finalBook.getAvailableCopy();

        /*
         * Print final state.
         */
        System.out.println();
        System.out.println("==========================================");
        System.out.println("CONCURRENCY TEST RESULT");
        System.out.println("==========================================");
        System.out.println(
                "Total requests     = " + requestCount);
        System.out.println(
                "Successful requests = " + successfulRequests);
        System.out.println(
                "Final available     = " + finalAvailableCopies);
        System.out.println("==========================================");

        /*
         * We must have a valid book.
         */
        assertNotNull(finalBook);

        /*
         * IMPORTANT INVARIANT:
         *
         * There was only ONE copy.
         *
         * Therefore availableCopy must never become negative.
         */
        assertEquals(
                0,
                finalAvailableCopies,
                "Available copies should be 0 after the only copy is borrowed");

        /*
         * Only ONE borrow should succeed.
         */
        assertEquals(
                1,
                successfulRequests,
                "Only one of the 10 concurrent requests should succeed");
    }
}