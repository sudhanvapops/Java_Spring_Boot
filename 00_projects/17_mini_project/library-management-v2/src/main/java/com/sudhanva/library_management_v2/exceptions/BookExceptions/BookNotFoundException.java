package com.sudhanva.library_management_v2.exceptions.BookExceptions;

public class BookNotFoundException extends RuntimeException{


    private BookNotFoundException(String message) {
        super(message);
    }

    public static BookNotFoundException byId(Long bookId) {
        return new BookNotFoundException("Book not found with id: " + bookId);
    }

    public static BookNotFoundException byAuthor(String authorName) {
        return new BookNotFoundException("Book not found with author: " + authorName);
    }

    public static BookNotFoundException byTitle(String title) {
        return new BookNotFoundException("Book not found with name: " + title);
    }

    public static BookNotFoundException byIsbn(String isbn) {
        return new BookNotFoundException("Book not found with isbn: " + isbn);
    }
}
