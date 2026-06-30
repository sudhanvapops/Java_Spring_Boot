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

import com.sudhanva.library.dto.BookDTO;
import com.sudhanva.library.dto.CreateBookRequest;
import com.sudhanva.library.dto.UpdateBookRequest;
import com.sudhanva.library.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> listBooks(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return bookService.listBooks(limit, offset);
    }

    @GetMapping("/{isbn}")
    public BookDTO getBook(@PathVariable String isbn) {
        return bookService.getByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @PutMapping("/{isbn}")
    public BookDTO updateBook(@PathVariable String isbn, @RequestBody UpdateBookRequest request) {
        return bookService.updateBook(isbn, request);
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
    }

    @PostMapping("/{isbn}/authors/{authorId}")
    public BookDTO addAuthor(@PathVariable String isbn, @PathVariable Long authorId) {
        return bookService.addAuthorToBook(isbn, authorId);
    }
}
