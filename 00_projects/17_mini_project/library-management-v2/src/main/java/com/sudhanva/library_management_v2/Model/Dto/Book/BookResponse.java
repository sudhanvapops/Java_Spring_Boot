package com.sudhanva.library_management_v2.Model.Dto.Book;

import lombok.Builder;

@Builder
public record BookResponse(
    Long id,
    String name,
    String author,
    Integer availableCopies,
    Integer totalCopies,
    Boolean isActive
) {
    
}
