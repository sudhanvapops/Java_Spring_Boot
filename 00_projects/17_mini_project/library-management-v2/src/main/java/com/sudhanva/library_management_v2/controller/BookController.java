package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.Service.BookService;
import com.sudhanva.library_management_v2.security.authorization.StaffOnly;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller
@RestController
@RequestMapping("/api/book")
@Tag(name = "Books", description = "Endpoints for managing the book catalog")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Get Book By Id
    @Operation(summary = "Get a book by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book found",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
            @Parameter(description = "Book id") @PathVariable Long id
        ) {
        ApiResponse<BookResponse> response = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get Book By Name
    @Operation(summary = "Get books by name")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Matching books found",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given name",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByName(
        @Parameter(description = "Book name to search for") @RequestParam String name
    ) {
        ApiResponse<List<BookResponse>> response = bookService.getBookByBookName(name);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get Book By Isbn
    @Operation(summary = "Get a book by isbn")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book found",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given isbn",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/isbn")
    public ResponseEntity<ApiResponse<BookResponse>> getBookByIsbn(
        @Parameter(description = "Isbn to search for") @RequestParam String isbn
    ) {
        ApiResponse<BookResponse> response = bookService.getBookByIsbn(isbn);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get Book By Author
    @Operation(summary = "Get books by author")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Matching books found",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given author",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/author")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBookByAuthor(
        @Parameter(description = "Author name to search for") @RequestParam String author
    ) {
        ApiResponse<List<BookResponse>> response = bookService.getBookByAuthor(author);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get All Book
    @Operation(summary = "Get all books")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books found",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No books exist in the library",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        ApiResponse<List<BookResponse>> response = bookService.getAllBooks();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Add Book
    @Operation(summary = "Add a new book")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book created",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation, or available copies exceed total copies")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A book with the same name and author already exists",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @StaffOnly
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> addBook(
        @Valid @RequestBody BookRequest bookRequest
    ) {

        ApiResponse<BookResponse> response = bookService.addBook(bookRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Update Book
    @Operation(summary = "Update an existing book")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book updated",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation, or available copies exceed total copies")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Book is inactive, or another book already has the same name and author",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @StaffOnly
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookById(
        @Parameter(description = "Book id") @PathVariable Long id,
        @Valid @RequestBody BookRequest bookRequest
    ) {

        ApiResponse<BookResponse> response = bookService.updateBookById(id, bookRequest);
        return ResponseEntity.ok(response);
    }


    // Delete Book
    @Operation(summary = "Deactivate a book", description = "Soft-deletes a book by marking it inactive; it is not removed from the database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book deactivated",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Book is already inactive, or currently borrowed",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @StaffOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> deleteBook(
        @Parameter(description = "Book id") @PathVariable Long id
    ) {
        ApiResponse<BookResponse> response = bookService.deleteBook(id);
        return ResponseEntity.ok(response);
    }


    // Reactivate Book
    @Operation(summary = "Reactivate a previously deactivated book")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book reactivated",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No book exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Book is already active "
        + "(currently returns a generic server error - no dedicated handler wired up for this case yet)")
    @StaffOnly
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BookResponse>> activateBook(
        @Parameter(description = "Book id") @PathVariable Long id
    ) {

        ApiResponse<BookResponse> response = bookService.activateBook(id);
        return ResponseEntity.ok(response);
    }

}
