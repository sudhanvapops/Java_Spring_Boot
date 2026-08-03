package com.sudhanva.library_management_v2.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Book.BookRequest;
import com.sudhanva.library_management_v2.Model.Dto.Book.BookResponse;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookAlreadyActiveException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookCurrentlyBorrowedException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookNotFoundException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookInactiveException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.InvalidBookCopiesException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.NoBooksFoundException;
import com.sudhanva.library_management_v2.repo.BookRepo;

@Service
public class BookService {

    private final BookRepo bookRepo;

    BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    // Utility Methods

    private String normalizeString(String s) {
        return s.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private Book mapToBook(BookRequest bookRequest) {

        return Book.builder()
                .name(normalizeString(bookRequest.name()))
                .author(normalizeString(bookRequest.author()))
                .availableCopy(bookRequest.availableCopies())
                .totalCopies(bookRequest.totalCopies())
                .isActive(bookRequest.isActive())
                .build();
    }

    private BookResponse mapToBookResponse(Book book) {

        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .availableCopies(book.getAvailableCopy())
                .totalCopies(book.getTotalCopies())
                .isActive(book.getIsActive())
                .build();
    }

    // Service Methods

    // Get All Book
    @Transactional(readOnly = true)
    public ApiResponse<List<BookResponse>> getAllBooks() {

        List<Book> books = bookRepo.findAll();

        if (books.isEmpty()) {
            throw new NoBooksFoundException();
        }

        List<BookResponse> bookResponse = books
                .stream()
                .map((book) -> mapToBookResponse(book))
                .toList();

        return new ApiResponse<>(
                true,
                "Books Found: " + books.size(),
                bookResponse);

    }

    // Get Book By Id
    @Transactional(readOnly = true)
    public ApiResponse<BookResponse> getBookById(Long id) {

        Book book = bookRepo.findById(id).orElseThrow(
                () -> BookNotFoundException.byId(id)
        );

        return new ApiResponse<>(
            true,
            "Book Found: " + id,
            mapToBookResponse(book)
        );

    }

    // Find book by Book Author
    @Transactional(readOnly = true)
    public ApiResponse<List<BookResponse>> getBookByAuthor(String author) {

        List<Book> books = bookRepo.findByAuthor(normalizeString(author));

        if (books.isEmpty()) {
            throw BookNotFoundException.byAuthor(author);
        }

        List<BookResponse> bookResponse = books
                .stream()
                .map((book) -> mapToBookResponse(book))
                .toList();

        return new ApiResponse<>(
                true,
                "Book Found: " + author + " Count: " + books.size(),
                bookResponse);
    }

    // Find book by Book Name
    @Transactional(readOnly = true)
    public ApiResponse<List<BookResponse>> getBookByBookName(String name) {
        List<Book> books = bookRepo.findByName(normalizeString(name));

        if (books.isEmpty()) {
            throw BookNotFoundException.byTitle(name);
        }

        List<BookResponse> bookResponse = books
                .stream()
                .map((book) -> mapToBookResponse(book))
                .toList();

        return new ApiResponse<>(
                true,
                "Book Found: " + name + " Count: " + books.size(),
                bookResponse);
    }

    // Add Book
    @Transactional
    public ApiResponse<BookResponse> addBook(BookRequest bookRequest) {

        Book existingBook = bookRepo.findByNameAndAuthor(
                normalizeString(bookRequest.name()),
                normalizeString(bookRequest.author())
            ).orElse(null);


        if (existingBook != null) {
            throw new BookAlreadyExistsException(
                bookRequest.name(), bookRequest.author()
            );
        }

        if (bookRequest.availableCopies() > bookRequest.totalCopies()) {
           throw new InvalidBookCopiesException(
                bookRequest.availableCopies(),
                bookRequest.totalCopies()
           );
        }

        Book book = mapToBook(bookRequest);

        return new ApiResponse<>(
                true,
                "Book Saved",
                mapToBookResponse(bookRepo.save(book)));
    }


    // Update Book
    @Transactional
    public ApiResponse<BookResponse> updateBookById(
            Long id, BookRequest bookRequest) {

        // Find old copy
        Book existingBook = bookRepo.findById(id).orElseThrow(
            () -> BookNotFoundException.byId(id)
        );


        if (bookRequest.totalCopies() < bookRequest.availableCopies()) {
            throw new InvalidBookCopiesException(
                bookRequest.availableCopies(),
                bookRequest.totalCopies()
           );
        }

        // Check New Name and Author book is unique
        Book anotherBook = bookRepo.findByNameAndAuthor(
                normalizeString(bookRequest.name()),
                normalizeString(bookRequest.author())).orElse(null);


        if (anotherBook != null && !anotherBook.getId().equals(id)) {
            throw new BookAlreadyExistsException(
                bookRequest.name(),
                bookRequest.author()
            );
        }


        if (Boolean.FALSE.equals(existingBook.getIsActive())) {
            throw new BookInactiveException();
        }

        existingBook.setName(normalizeString(bookRequest.name()));
        existingBook.setAuthor(normalizeString(bookRequest.author()));
        
        // TODO: Error can happen In setting Available Copies
        // Derive it 
        existingBook.setAvailableCopy(bookRequest.availableCopies());
        existingBook.setTotalCopies(bookRequest.totalCopies());

        Book updatedbook = bookRepo.save(existingBook);

        return new ApiResponse<>(
                true,
                "Book Updated: " + id,
                mapToBookResponse(updatedbook));
    }


    // Book Delete
    @Transactional
    public ApiResponse<BookResponse> deleteBook(Long id) {

        Book book = bookRepo.findById(id).orElseThrow(
            () -> BookNotFoundException.byId(id)
        );


        if (!book.getIsActive()) {
            throw new BookInactiveException();
        }

        boolean isCurrentlyBorrowed = book.getBorrowRecords()
                .stream()
                .anyMatch(record -> record.getReturnDate() == null);

        if (isCurrentlyBorrowed) {
            throw new BookCurrentlyBorrowedException();
        }

        book.setIsActive(false);

        return new ApiResponse<>(
                true,
                "Book deactivated successfully: " + id,
                mapToBookResponse(book));
    }


    // Activate Book
    @Transactional
    public ApiResponse<BookResponse> activateBook(Long id) {

        Book existingBook = bookRepo.findById(id).orElseThrow(
            () -> BookNotFoundException.byId(id)
        );


        // If already active, no need to validate further
        if(existingBook.getIsActive()){
            throw new BookAlreadyActiveException(id);
        }
        existingBook.setIsActive(true);

        Book book = bookRepo.save(existingBook);

        return new ApiResponse<>(
                true,
                "Book Reactivated: " + id,
                mapToBookResponse(book));
    }
}
