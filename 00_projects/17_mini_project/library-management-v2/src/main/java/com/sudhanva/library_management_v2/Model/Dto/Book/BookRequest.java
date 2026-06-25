package com.sudhanva.library_management_v2.Model.Dto.Book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record BookRequest(
    @NotBlank(message = "Book name is required")
    String name,

    @NotBlank(message = "Author is required")
    String author,

    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    Integer totalCopies
) {
    
}
