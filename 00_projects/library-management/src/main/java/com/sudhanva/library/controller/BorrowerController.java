package com.sudhanva.library.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sudhanva.library.dto.BorrowerDTO;
import com.sudhanva.library.dto.CreateBorrowerRequest;
import com.sudhanva.library.dto.UpdateBorrowerRequest;
import com.sudhanva.library.service.BorrowerService;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @GetMapping
    public List<BorrowerDTO> listBorrowers(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return borrowerService.listBorrowers(limit, offset);
    }

    @GetMapping("/{cardNumber}")
    public BorrowerDTO getBorrower(@PathVariable String cardNumber) {
        return borrowerService.getByCardNumber(cardNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowerDTO createBorrower(@RequestBody CreateBorrowerRequest request) {
        return borrowerService.createBorrower(request);
    }

    @PutMapping("/{cardNumber}")
    public BorrowerDTO updateBorrower(@PathVariable String cardNumber, @RequestBody UpdateBorrowerRequest request) {
        return borrowerService.updateBorrower(cardNumber, request);
    }

    @DeleteMapping("/{cardNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBorrower(@PathVariable String cardNumber) {
        borrowerService.deleteBorrower(cardNumber);
    }
}
