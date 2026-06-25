package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record BorrowTransactionResponse(
    Long transactionId,
    String memberName,
    LocalDateTime borrowDate,
    LocalDateTime dueDate,
    List<BorrowTransactionItemResponse> books
) {
    
}
