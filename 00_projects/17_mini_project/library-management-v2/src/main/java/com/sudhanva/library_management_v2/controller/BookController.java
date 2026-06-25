package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get Book By Name
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByName(
        @RequestParam String name
    ){
        ApiResponse<List<BookResponse>> response = bookService.getBookByBookName(name);
        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get Book By Author
    @GetMapping("/author")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByAuthor(
        @RequestParam String author
    ){
        ApiResponse<List<BookResponse>> response = bookService.getBookByAuthor(author);
        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get All Book
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks( ){
        ApiResponse<List<BookResponse>> response = bookService.getAllBooks();
        if (response.success() == false){
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
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookById(
        @PathVariable Long id,
        @Valid @RequestBody BookRequest bookRequest
    ){

        ApiResponse<BookResponse> response = bookService.updateBookById(id,bookRequest);

        if (response.success() == false){
            if (response.message().contains("doesnt exist")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }   
        
        return ResponseEntity.ok(response);
    }


    // Update Total Copies

    // Update Available Copies

    // Delete Book

}
