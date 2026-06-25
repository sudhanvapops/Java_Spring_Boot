package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BorrowTransactionItemRequest(
    @NotNull(message = "Book id is required")
    Long bookId
) {
    
}
