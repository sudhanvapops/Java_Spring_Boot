package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record BorrowRecordResponse(
    Long memberId,
    String memberName,
    
    Long borrowedBookId,
    String bookName,
    String author,
    LocalDateTime dueDate
) {
    
}
