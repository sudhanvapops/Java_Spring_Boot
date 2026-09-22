package com.sudhanva.library.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.sudhanva.library.entity.Book;

public class BookDTO {

    public Long id;
    public String bookName;
    public String isbn;
    public Integer totalCopies;
    public Integer availableCopies;
    public Set<String> authorNames;

    public static BookDTO from(Book book) {
        BookDTO dto = new BookDTO();
        dto.id = book.getId();
        dto.bookName = book.getBookName();
        dto.isbn = book.getIsbn();
        dto.totalCopies = book.getTotalCopies();
        dto.availableCopies = book.getAvailableCopies();
        dto.authorNames = book.getAuthors().stream()
                .map(a -> a.getAuthorName())
                .collect(Collectors.toSet());
        return dto;
    }
}
