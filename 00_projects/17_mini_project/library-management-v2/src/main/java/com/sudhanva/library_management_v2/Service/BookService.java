package com.sudhanva.library_management_v2.Service;

import com.sudhanva.library_management_v2.repo.MemberRepo;
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

    private String normalizeString(String s){
        return s.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }


    private Book mapToBook(BookRequest bookRequest){

        return Book.builder()
                .name(
                    normalizeString(bookRequest.name())
                )
                .author(
                    normalizeString(bookRequest.author())
                )
                .availableCopy(bookRequest.availableCopy())
                .totalCopies(bookRequest.totalCopies())
                .build();
    }

    

    private BookResponse mapToBookResponse(Book book){

        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .availableCopies(book.getAvailableCopy())
                .totalCopies(book.getTotalCopies())
                .build();
    }


    // Service Methods

    // Add Book
    public ApiResponse<BookResponse> addBook(BookRequest bookRequest) {

        Book existingBook = bookRepo.findByNameAndAuthor(
            normalizeString(bookRequest.name()),
            normalizeString(bookRequest.author())
        );

        if (existingBook != null){
            return new ApiResponse<>(
                false,
                "Book Already Exist",
                mapToBookResponse(existingBook)
            );  
        }

        Book book = mapToBook(bookRequest);

        return new ApiResponse<>(
                true,
                "Book Saved",
                mapToBookResponse(bookRepo.save(book))
        );  
    }
    
}
