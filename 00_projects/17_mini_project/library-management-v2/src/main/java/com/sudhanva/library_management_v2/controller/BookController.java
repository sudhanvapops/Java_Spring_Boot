package com.sudhanva.library_management_v2.controller;

import com.sudhanva.library_management_v2.repo.BookRepo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
        @PathVariable Long id
    ){
        ApiResponse<BookResponse> response = bookService.getBookById(id);
        if (response == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get Book By Name
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByName(
        @PathVariable String name
    ){
        ApiResponse<List<BookResponse>> response = bookService.getBookByBookName(name);
        if (response == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get Book By Author
    @GetMapping("/author/{author}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByAuthor(
        @PathVariable String author
    ){
        ApiResponse<List<BookResponse>> response = bookService.getBookByAuthor(author);
        if (response == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get All Book
     @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByAuthor( ){
        ApiResponse<List<BookResponse>> response = bookService.getAllBooks();
        if (response == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


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
