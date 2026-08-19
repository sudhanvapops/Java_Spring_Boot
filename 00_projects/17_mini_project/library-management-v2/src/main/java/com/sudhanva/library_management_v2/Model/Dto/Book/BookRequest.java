package com.sudhanva.library_management_v2.Model.Dto.Book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;


@Builder
public record BookRequest(
    @NotBlank(message = "Book name is required")
    String name,

    @NotBlank(message = "Author is required")
    String author,

    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    Integer totalCopies,

    @NotNull(message = "Available copies is required")
    @Min(value = 0, message = "Total copies must be at least 0")
    Integer availableCopies,

    @NotBlank(message = "ISBN is required")
    @Pattern(
        regexp = "^(\\d{9}[\\dXx]|\\d{13})$",
        message = "ISBN must be a valid ISBN-10 or ISBN-13 (digits only, no hyphens)"
    )
    String isbn,

    @NotNull(message = "isActive cannot be null")
    Boolean isActive

) {

    public BookRequest {
        if (isActive == null) {
            isActive = true;
        }
    }
    
}
