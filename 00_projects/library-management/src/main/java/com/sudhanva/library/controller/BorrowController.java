package com.sudhanva.library.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library.dto.BorrowRecordDTO;
import com.sudhanva.library.service.BorrowService;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    public List<BorrowRecordDTO> listRecords(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return borrowService.listRecords(limit, offset);
    }

    @GetMapping("/borrower/{cardNumber}")
    public List<BorrowRecordDTO> listForBorrower(@PathVariable String cardNumber) {
        return borrowService.listRecordsForBorrower(cardNumber);
    }

    @PostMapping("/borrow")
    public BorrowRecordDTO borrowBook(@RequestParam String cardNumber, @RequestParam String isbn) {
        return borrowService.borrowBook(cardNumber, isbn);
    }

    @PostMapping("/return")
    public BorrowRecordDTO returnBook(@RequestParam String cardNumber, @RequestParam String isbn) {
        return borrowService.returnBook(cardNumber, isbn);
    }
}
