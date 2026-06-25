package com.sudhanva.library_management_v2.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Book.BookRequest;
import com.sudhanva.library_management_v2.Model.Dto.Book.BookResponse;
import com.sudhanva.library_management_v2.repo.BookRepo;

@Service
public class BookService {

    @Autowired
    private BookRepo bookRepo;

    // Utility Methods

    private String normalizeString(String s) {
        return s.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private Book mapToBook(BookRequest bookRequest) {

        return Book.builder()
                .name(
                        normalizeString(bookRequest.name()))
                .author(
                        normalizeString(bookRequest.author()))
                .availableCopy(bookRequest.availableCopy())
                .totalCopies(bookRequest.totalCopies())
                .build();
    }

    private BookResponse mapToBookResponse(Book book) {

        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .availableCopies(book.getAvailableCopy())
                .totalCopies(book.getTotalCopies())
                .build();
    }

    // Service Methods

    // Get All Book
    public ApiResponse<List<BookResponse>> getAllBooks() {

        List<Book> books = bookRepo.findAll();

        if (books.isEmpty()) {
            return new ApiResponse<>(
                    false,
                    "Book doesnt exsit",
                    null);
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
    public ApiResponse<BookResponse> getBookById(Long id) {

        Book book = bookRepo.findById(id).orElse(null);

        if (book == null) {
            return new ApiResponse<>(
                    false,
                    "Book doesnt exsit",
                    null);
        }

        return new ApiResponse<>(
                true,
                "Book Found: " + id,
                mapToBookResponse(book));

    }

    // Find book by Book Author
    public ApiResponse<List<BookResponse>> getBookByAuthor(String author) {
        List<Book> books = bookRepo.findByAuthor(normalizeString(author));

        if (books.isEmpty()) {
            return new ApiResponse<>(
                    false,
                    "No Book exsits",
                    null);
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
    public ApiResponse<List<BookResponse>> getBookByBookName(String name) {
        List<Book> books = bookRepo.findByName(normalizeString(name));

        if (books.isEmpty()) {
            return new ApiResponse<>(
                    false,
                    "No Book exsits",
                    null);
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
    public ApiResponse<BookResponse> addBook(BookRequest bookRequest) {

        Book existingBook = bookRepo.findByNameAndAuthor(
                normalizeString(bookRequest.name()),
                normalizeString(bookRequest.author())).orElse(null);

        if (existingBook != null) {
            return new ApiResponse<>(
                    false,
                    "Book Already Exist",
                    mapToBookResponse(existingBook));
        }

        Book book = mapToBook(bookRequest);

        return new ApiResponse<>(
                true,
                "Book Saved",
                mapToBookResponse(bookRepo.save(book)));
    }


    // Update Book
    public ApiResponse<BookResponse> updateBookById(
            Long id, BookRequest bookRequest) {


        Book anotherBook = bookRepo.findByNameAndAuthor(
                normalizeString(bookRequest.name()),
                normalizeString(bookRequest.author())).orElse(null);

        if (anotherBook != null && !anotherBook.getId().equals(id)) {
            return new ApiResponse<>(
                    false,
                    "Another book with same name and author exists",
                    mapToBookResponse(anotherBook)
            );
        }

        Book existingBook = bookRepo.findById(id).orElse(null);

        if (existingBook == null) {
            return new ApiResponse<>(
                    false,
                    "Book doesnt exist: " + id,
                    null);
        }


        existingBook.setName(normalizeString(bookRequest.name()));
        existingBook.setAuthor(normalizeString(bookRequest.author()));
        existingBook.setAvailableCopy(bookRequest.availableCopy());
        existingBook.setTotalCopies(bookRequest.totalCopies());


        Book updatedbook = bookRepo.save(existingBook);

        return new ApiResponse<>(
                true,
                "Book Updated: " + id,
                mapToBookResponse(updatedbook)
        );
    }




}
