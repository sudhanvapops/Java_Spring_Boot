package com.sudhanva.library_management_v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Book.BookRequest;
import com.sudhanva.library_management_v2.Model.Dto.Book.BookResponse;
import com.sudhanva.library_management_v2.Service.BookService;

import jakarta.validation.Valid;

@Controller
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;
    
    // Get Book By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(){
        return null;
    }

    // Get All Book

    // Add Book
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> addBook(
        @Valid @RequestBody BookRequest bookRequest
    ){

        ApiResponse<BookResponse> response = bookService.addBook(bookRequest);

        // Duplicate Book
        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update Book

    // Delete Book

}
